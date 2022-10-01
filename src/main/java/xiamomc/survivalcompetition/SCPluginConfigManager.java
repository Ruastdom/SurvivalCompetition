package xiamomc.survivalcompetition;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.pluginbase.XiaMoJavaPlugin;
import xiamomc.survivalcompetition.Misc.StageInfo;
import xiamomc.survivalcompetition.Misc.TeamInfo;

public class SCPluginConfigManager extends PluginConfigManager
{
    static
    {
        ConfigurationSerialization.registerClass(TeamInfo.class);
        ConfigurationSerialization.registerClass(StageInfo.class);
    }

    public SCPluginConfigManager(XiaMoJavaPlugin plugin)
    {
        super(plugin);
    }
}
