package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;

public interface IMultiverseManager
{
    boolean createWorlds(String worldName);

    boolean deleteWorlds(String worldName);

    void createSMPWorldGroup(String worldName);

    boolean linkSMPWorlds(String worldName);

    void tpToWorld(Player player, String worldName);
}
