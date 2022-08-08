package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalCompetition extends JavaPlugin {
    public static JavaPlugin instance;
    @Override
    public void onEnable() {
        getLogger().info("SurvivalCompetition is starting...");
        instance = this;
        if (Bukkit.getPluginCommand("joinsg") != null) {
            Bukkit.getPluginCommand("joinsg").setExecutor(new JoiningGameCommand());
        }
        Bukkit.getPluginManager().registerEvents(new EventProcessor(), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
