package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.slf4j.Logger;
import xiamomc.pluginbase.Command.CommandHelper;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.pluginbase.ScheduleInfo;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xiamomc.survivalcompetition.Command.SCCommandHelper;
import xiamomc.survivalcompetition.Managers.*;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SurvivalCompetition extends XiaMoJavaPlugin
{
    public static SurvivalCompetition instance;
    private GameManager gameManager;
    private PlayerListManager playerListManager;
    private CareerManager careerManager;
    private IMultiverseManager multiverseManager;
    private TeamManager teamManager;
    private SCCommandHelper cmdHelper;
    private PluginConfigManager config;
    private Logger logger = this.getSLF4JLogger();

    public static SurvivalCompetition GetInstance()
    {
        return instance;
    }

    @Override
    public String getNameSpace()
    {
        return "survivalcompetition";
    }

    public SurvivalCompetition()
    {
        if (instance != null)
            logger.warn("之前似乎已经创建过一个插件实例了...除非你是故意这么做的，不然可能代码又有哪里出bug了！");

        instance = this;
        cmdHelper = new SCCommandHelper();
    }

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        logger.info("Enabling SurvivalCompetition...");

        //region 注册依赖

        //先反注册一遍所有依赖再注册插件
        dependencyManager.UnCacheAll();

        processExceptionCount();

        dependencyManager.Cache(this);
        dependencyManager.CacheAs(PluginConfigManager.class, config = new SCPluginConfigManager(this));

        var allowDebug = config.get(Boolean.class, ConfigNode.create().Append("DevelopmentMode"));
        if (allowDebug == null) allowDebug = false;

        if (allowDebug) logger.warn("将启用调试模式");

        dependencyManager.CacheAs(IGameManager.class, gameManager = new GameManager());
        dependencyManager.CacheAs(ITeamManager.class, teamManager = new TeamManager());
        dependencyManager.CacheAs(IPlayerListManager.class, playerListManager = new PlayerListManager());
        dependencyManager.CacheAs(ICareerManager.class, careerManager = new CareerManager());
        dependencyManager.CacheAs(IMultiverseManager.class, multiverseManager = allowDebug
                ? new DummyMVManager()
                : new MultiverseManager());
        dependencyManager.Cache(new PermissionUtils());
        dependencyManager.Cache(cmdHelper);

        //endregion

        this.schedule(c ->
        {
            Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
            Bukkit.getPluginManager().registerEvents(new CareerEventProcessor(), this);
        });

        this.shouldAbortTicking = false;

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::tick, 0, 1);
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        logger.info("Disabling SurvivalCompetition");

        //禁止tick
        this.shouldAbortTicking = true;

        //禁用时先结束游戏
        gameManager.endGame();

        //todo: 添加相关方法到multiverseManager或者gameManager里
        //卸载当前世界
        if (gameManager.CurrentWorldBaseName != null)
        {
            multiverseManager.deleteWorlds(gameManager.CurrentWorldBaseName);
            gameManager.CurrentWorldBaseName = null;
        }

        //反注册依赖
        dependencyManager.UnCacheAll();
    }

    //region tick相关

    private long currentTick = 0;

    private void tick()
    {
        currentTick += 1;

        if (shouldAbortTicking) return;

        var schedules = new ArrayList<>(runnables);
        schedules.forEach(c ->
        {
            if (currentTick - c.TickScheduled >= c.Delay)
            {
                runnables.remove(c);

                if (c.isCanceled()) return;

                //logger.info("执行：" + c + "，当前TICK：" + currentTick);
                try
                {
                    c.Function.accept(null);
                }
                catch (Exception e)
                {
                    this.onExceptionCaught(e, c);
                }
            }
        });

        schedules.clear();
    }

    //region tick异常捕捉与处理

    //一秒内最多能接受多少异常
    //todo: 之后考虑做进配置里让它可调？
    @SuppressWarnings("FieldCanBeLocal")
    private final int exceptionLimit = 5;

    //已经捕获的异常
    private int exceptionCaught = 0;

    //是否应该中断tick
    private boolean shouldAbortTicking = false;

    private boolean onExceptionCaught(Exception exception, ScheduleInfo scheduleInfo)
    {
        if (exception == null) return false;

        exceptionCaught += 1;

        logger.warn("执行" + scheduleInfo + "时捕获到未处理的异常：");
        exception.printStackTrace();

        if (exceptionCaught >= exceptionLimit)
        {
            logger.error("可接受异常已到达最大限制");
            this.setEnabled(false);
        }

        return true;
    }

    private void processExceptionCount()
    {
        exceptionCaught -= 1;

        this.schedule(c -> processExceptionCount(), 5);
    }

    //endregion tick异常捕捉与处理

    //endregion tick相关

    private final List<ScheduleInfo> runnables = new ArrayList<>();

    public ScheduleInfo schedule(Consumer<?> runnable)
    {
        return this.schedule(runnable, 1);
    }

    public ScheduleInfo schedule(Consumer<?> function, int delay)
    {
        var si = new ScheduleInfo(function, delay, currentTick);
        synchronized (runnables)
        {
            //Logger.info("添加：" + si + "，当前TICK：" + currentTick);
            runnables.add(si);
        }

        return si;
    }
}