package xiamomc.survivalcompetition.Configuration;

import org.bukkit.configuration.file.FileConfiguration;
import xiamomc.survivalcompetition.SurvivalCompetition;

public class PluginConfigManager implements IConfigManager
{
    private final FileConfiguration backendConfig;
    private final SurvivalCompetition plugin;

    public PluginConfigManager(SurvivalCompetition plugin, FileConfiguration config)
    {
        this.backendConfig = config;
        this.plugin = plugin;
    }

    @Override
    public Object Get(String path)
    {
        return backendConfig.get(path);
    }

    @Override
    public boolean Set(String path, Object value)
    {
        //spigot的配置管理器没有返回值
        backendConfig.set(path, value);
        plugin.saveConfig();
        return true;
    }

    @Override
    public boolean RestoreDefaults()
    {
        //没有返回值+1
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        return true;
    }

    @Override
    public void Refresh()
    {
        plugin.reloadConfig();
    }
}
