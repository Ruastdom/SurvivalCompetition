package xiamomc.survivalcompetition;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import org.bukkit.Bukkit;
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
        dependencyManager.CacheAs(IGameManager.class, gameManager = new GameManager());
        dependencyManager.CacheAs(ITeamManager.class, teamManager = new TeamManager());
        dependencyManager.CacheAs(IPlayerListManager.class, playerListManager = new PlayerListManager());
        dependencyManager.CacheAs(ICareerManager.class, careerManager = new CareerManager());
        dependencyManager.CacheAs(IMultiverseManager.class, multiverseManager = new MultiverseManager());

        cmdHelper = new CommandHelper();

        //endregion

        Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
        Bukkit.getPluginManager().registerEvents(new CareerEventProcessor(), this);

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

    private void tick()
    {
        var schedules = new ArrayList<>(runnables);
        runnables.clear();
        schedules.forEach(c -> c.accept(null));
    }

    private final List<Consumer<?>> runnables = new ArrayList<>();

    public void Schedule(Consumer<?> runnable)
    {
        synchronized (runnables)
        {
            runnables.add(runnable);
        }
    }
}
