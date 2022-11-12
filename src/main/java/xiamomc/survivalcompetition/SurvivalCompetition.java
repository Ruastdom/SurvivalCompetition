package xiamomc.survivalcompetition;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xiamomc.pluginbase.Messages.MessageStore;
import xiamomc.survivalcompetition.commands.SCCommandHelper;
import xiamomc.survivalcompetition.managers.*;
import xiamomc.survivalcompetition.messages.SCMessageStore;
import xiamomc.survivalcompetition.misc.permission.PermissionUtils;

public final class SurvivalCompetition extends XiaMoJavaPlugin
{
    public static SurvivalCompetition instance;
    private GameManager gameManager;
    private CareerManager careerManager;
    private IMultiverseManager multiverseManager;
    private TeamManager teamManager;
    private SCCommandHelper cmdHelper;
    private PluginConfigManager config;
    private Logger logger = this.getSLF4JLogger();

    public static SurvivalCompetition getInstance()
    {
        return instance;
    }

    public static String getSCNameSpace()
    {
        return "survivalcompetition";
    }

    @Override
    public String getNameSpace()
    {
        return getSCNameSpace();
    }

    public SurvivalCompetition()
    {
        instance = this;
        cmdHelper = new SCCommandHelper();
    }

    private final BindableList<Player> players = new BindableList<>();

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        super.onEnable();

        //region 注册依赖

        dependencyManager.cache(this);
        dependencyManager.cacheAs(PluginConfigManager.class, config = new SCPluginConfigManager(this));

        var core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        var portals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
        var inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");

        dependencyManager.cache(core);
        dependencyManager.cache(portals);
        dependencyManager.cache(inventories);

        var allowDebug = config.get(Boolean.class, ConfigNode.create().append("DevelopmentMode"));
        if (allowDebug == null) allowDebug = false;

        if (allowDebug) logger.warn("将启用调试模式");

        dependencyManager.cache(players);

        dependencyManager.cacheAs(IGameManager.class, gameManager = new GameManager());
        dependencyManager.cacheAs(ITeamManager.class, teamManager = new TeamManager());
        dependencyManager.cacheAs(ICareerManager.class, careerManager = new CareerManager());
        dependencyManager.cacheAs(IMultiverseManager.class, multiverseManager = allowDebug
                ? new DummyMVManager()
                : new MultiverseManager());
        dependencyManager.cacheAs(MessageStore.class, new SCMessageStore());
        dependencyManager.cache(new PermissionUtils());
        dependencyManager.cache(cmdHelper);

        //endregion

        this.schedule(c ->
        {
            Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
            Bukkit.getPluginManager().registerEvents(new CareerEventProcessor(), this);
        });
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

        try
        {
            //禁用时先结束游戏
            gameManager.endGame();

            //todo: 添加相关方法到multiverseManager或者gameManager里
            //卸载当前世界
            if (gameManager.CurrentWorldBaseName != null)
            {
                multiverseManager.deleteWorlds(gameManager.CurrentWorldBaseName);
                gameManager.CurrentWorldBaseName = null;
            }
        }
        catch (Throwable t)
        {
            logger.warn("禁用时出现问题：" + t.getMessage());
            t.getStackTrace();
        }

        super.onDisable();
    }

}