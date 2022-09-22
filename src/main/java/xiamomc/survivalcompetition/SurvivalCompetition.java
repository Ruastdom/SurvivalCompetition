package xiamomc.survivalcompetition;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.survivalcompetition.Command.CommandHelper;
import xiamomc.survivalcompetition.Managers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class SurvivalCompetition extends JavaPlugin
{
    public static SurvivalCompetition instance;
    private GameManager gameManager;
    private PlayerListManager playerListManager;
    private CareerManager careerManager;
    private IMultiverseManager multiverseManager;
    private TeamManager teamManager;
    private CommandHelper cmdHelper;

    public static SurvivalCompetition GetInstance()
    {
        return instance;
    }

    private final GameDependencyManager dependencyManager;

    public SurvivalCompetition()
    {
        if (instance != null)
            getLogger().warning("之前似乎已经创建过一个插件实例了...除非你是故意这么做的，不然可能代码又有哪里出bug了！");

        instance = this;
        dependencyManager = new GameDependencyManager();
        cmdHelper = new CommandHelper();
    }

    public static MultiverseCore getMultiverseCore()
    {
        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        return core;
    }

    public static MultiverseInventories getMultiverseInventories()
    {
        MultiverseInventories inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");
        return inventories;
    }

    public static MultiverseNetherPortals getMultiverseNetherPortals()
    {
        MultiverseNetherPortals netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
        return netherportals;
    }

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        getLogger().info("Enabling SurvivalCompetition...");

        //region 注册依赖

        //先反注册一遍所有依赖再注册插件
        dependencyManager.UnCacheAll();

        dependencyManager.Cache(this);
        dependencyManager.CacheAs(FileConfiguration.class, this.getConfig());
        dependencyManager.CacheAs(IGameManager.class, gameManager = new GameManager());
        dependencyManager.CacheAs(ITeamManager.class, teamManager = new TeamManager());
        dependencyManager.CacheAs(IPlayerListManager.class, playerListManager = new PlayerListManager());
        dependencyManager.CacheAs(ICareerManager.class, careerManager = new CareerManager());
        dependencyManager.CacheAs(IMultiverseManager.class, multiverseManager = new MultiverseManager());

        //endregion

        this.Schedule(c ->
        {
            Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
            Bukkit.getPluginManager().registerEvents(new CareerEventProcessor(), this);
        });

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::tick, 0, 1);
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
        getLogger().info("Disabling SurvivalCompetition");

        //禁用时先结束游戏
        gameManager.endGame(playerListManager.getList());

        //反注册依赖
        dependencyManager.UnCacheAll();
    }

    private long currentTick = 0;

    private void tick()
    {
        currentTick += 1;

        var schedules = new ArrayList<>(runnables);
        schedules.forEach(c ->
        {
            if (currentTick - c.TickScheduled >= c.Delay)
            {
                //getLogger().info("执行：" + c + "，当前TICK：" + currentTick);
                c.Function.accept(null);
                runnables.remove(c);
            }
        });
        schedules.clear();
    }

    private final List<ScheduleInfo> runnables = new ArrayList<>();

    public void Schedule(Consumer<?> runnable)
    {
        this.Schedule(runnable, 1);
    }

    public void Schedule(Consumer<?> function, int delay)
    {
        synchronized (runnables)
        {
            var si = new ScheduleInfo(function, delay, currentTick);
            //getLogger().info("添加：" + si + "，当前TICK：" + currentTick);
            runnables.add(si);
        }
    }

    private static class ScheduleInfo
    {
        public Consumer<?> Function;
        public int Delay;
        public long TickScheduled;

        public ScheduleInfo(Consumer<?> function, int delay, long tickScheduled)
        {
            this.Function = function;
            this.Delay = delay;
            this.TickScheduled = tickScheduled;
        }

        @Override
        public String toString()
        {
            return "于第" + this.TickScheduled + "刻创建，"
                    + "并计划于" + this.Delay + "刻后执行的计划任务"
                    + "（" +this.Function + "）";
        }
    }
}
