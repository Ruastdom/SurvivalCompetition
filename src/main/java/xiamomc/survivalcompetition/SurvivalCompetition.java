package xiamomc.survivalcompetition;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xiamomc.survivalcompetition.Exceptions.DependencyAlreadyRegistedException;
import xiamomc.survivalcompetition.Managers.*;

import java.util.ArrayList;
import java.util.List;

public final class SurvivalCompetition extends JavaPlugin {
    public static SurvivalCompetition instance;
    private GameManager gameManager;
    private PlayerListManager playerListManager;
    private CareerManager careerManager;
    private TeamManager teamManager;

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

    public static MultiverseCore getMultiverseCore = null;
    public static MultiverseInventories getMultiverseInventories = null;
    public static MultiverseNetherPortals getMultiverseNetherPortals = null;

    public static MultiverseCore getMultiverseCore() {
        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        return core;
    }

    public static MultiverseInventories getMultiverseInventories() {
        MultiverseInventories inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");
        return inventories;
    }

    public static MultiverseNetherPortals getMultiverseNetherPortals() {
        MultiverseNetherPortals netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
        return netherportals;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Enabling SurvivalCompetition...");

        // 获取多世界插件
        MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        MultiverseInventories inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");
        MultiverseNetherPortals netherportals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");

        //region 注册依赖

        //先反注册一遍所有依赖再注册插件
        dependencyManager.UnCacheAll();

        //目前只能再throw一遍:(
        try {
            dependencyManager.Cache(this);
            dependencyManager.CacheAs(IGameManager.class, gameManager = new GameManager());
            dependencyManager.CacheAs(ITeamManager.class, teamManager = new TeamManager());
            dependencyManager.CacheAs(IPlayerListManager.class, playerListManager = new PlayerListManager());
            dependencyManager.CacheAs(ICareerManager.class, careerManager = new CareerManager());
        } catch (DependencyAlreadyRegistedException e) {
            throw new RuntimeException(e);
        }

        //endregion

        if (Bukkit.getPluginCommand("joinsg") != null) {
            Bukkit.getPluginCommand("joinsg").setExecutor(new JoiningGameCommand());
        }
        if (Bukkit.getPluginCommand("setcareer") != null) {
            Bukkit.getPluginCommand("setcareer").setExecutor(new CareerCommandProcessor());
        }
        Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
        Bukkit.getPluginManager().registerEvents(new CareerEventProcessor(), this);

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, this::tick, 0, 1);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabling SurvivalCompetition");

        //禁用时先结束游戏
        gameManager.endGame(playerListManager.getList());

        //反注册依赖
        dependencyManager.UnCacheAll();
    }

    private void tick()
    {
        runnables.forEach(Runnable::run);
        runnables.clear();
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
