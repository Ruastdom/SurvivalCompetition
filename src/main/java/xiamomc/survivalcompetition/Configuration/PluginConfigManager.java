package xiamomc.survivalcompetition.Configuration;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.Nullable;
import xiamomc.survivalcompetition.Misc.StageInfo;
import xiamomc.survivalcompetition.Misc.TeamInfo;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PluginConfigManager implements IConfigManager
{
    static
    {
        ConfigurationSerialization.registerClass(TeamInfo.class);
        ConfigurationSerialization.registerClass(StageInfo.class);
    }

    private FileConfiguration backendConfig;
    private final SurvivalCompetition plugin;

    public PluginConfigManager(SurvivalCompetition plugin)
    {
        this.plugin = plugin;
        this.Reload();
    }

    @Override
    @Nullable
    public <T> T Get(Class<T> type, ConfigNode node)
    {
        var value = backendConfig.get(node.toString());

        if (value == null)
        {
            return null;
        }

        //检查是否可以cast过去
        if (!type.isAssignableFrom(value.getClass()))
        {
            plugin.getSLF4JLogger().warn("未能将处于" + node + "的配置转换为" + type.getSimpleName());
            return null;
        }

        return (T) backendConfig.get(node.toString());
    }

    @Override
    public boolean Set(ConfigNode node, Object value)
    {
        //spigot的配置管理器没有返回值
        backendConfig.set(node.toString(), value);
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
    public void Reload()
    {
        plugin.reloadConfig();
        backendConfig = plugin.getConfig();

        for (var c : onRefresh)
            c.accept(null);

        plugin.saveConfig();
    }

    private final ArrayList<Consumer<?>> onRefresh = new ArrayList<>();

    @Override
    public boolean OnConfigRefresh(Consumer<?> c)
    {
        if (onRefresh.contains(c)) return false;
        onRefresh.add(c);
        return true;
    }

    @Override
    public boolean OnConfigRefresh(Consumer<?> c, boolean runOnce)
    {
        var ocr = this.OnConfigRefresh(c);

        if (!ocr) return false;

        c.accept(null);

        return true;
    }
}
