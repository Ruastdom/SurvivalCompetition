package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.slf4j.Logger;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xiamomc.survivalcompetition.commands.SCCommandHelper;
import xiamomc.survivalcompetition.managers.*;
import xiamomc.survivalcompetition.misc.permission.PermissionUtils;

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

    public static SurvivalCompetition getInstance()
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
        instance = this;
        cmdHelper = new SCCommandHelper();
    }

    @Override
    public void onEnable()
    {
        // Plugin startup logic
        super.onEnable();

        //region 注册依赖

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
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic

        //禁用时先结束游戏
        gameManager.endGame();

        //todo: 添加相关方法到multiverseManager或者gameManager里
        //卸载当前世界
        if (gameManager.CurrentWorldBaseName != null)
        {
            multiverseManager.deleteWorlds(gameManager.CurrentWorldBaseName);
            gameManager.CurrentWorldBaseName = null;
        }

        super.onDisable();
    }

}