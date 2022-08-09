package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.survivalcompetition.Exceptions.DependencyAlreadyRegistedException;
import xiamomc.survivalcompetition.Managers.*;

import java.util.ArrayList;
import java.util.List;

public final class SurvivalCompetition extends JavaPlugin {
    public static SurvivalCompetition instance;

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
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabling SurvivalCompetition...");

        //region 注册依赖

        //先反注册一遍所有依赖再注册插件
        dependencyManager.UnCacheAll();

        //目前只能再throw一遍:(
        try {
            dependencyManager.Cache(this);
            dependencyManager.CacheAs(IGameManager.class, new GameManager());
            dependencyManager.CacheAs(ITeamManager.class, new TeamManager());
            dependencyManager.CacheAs(IPlayerListManager.class, new PlayerListManager());
        } catch (DependencyAlreadyRegistedException e) {
            throw new RuntimeException(e);
        }

        //endregion

        if (Bukkit.getPluginCommand("joinsg") != null) {
            Bukkit.getPluginCommand("joinsg").setExecutor(new JoiningGameCommand());
        }
        Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::tick, 0, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabling SurvivalCompetition");

        //反注册依赖
        dependencyManager.UnCacheAll();
    }

    private void tick()
    {
        runnables.forEach(Runnable::run);
    }

    private final List<Runnable> runnables = new ArrayList<Runnable>();

    public void Schedule(Runnable runnable)
    {
        synchronized (runnables)
        {
            runnables.add(runnable);
        }
    }
}
