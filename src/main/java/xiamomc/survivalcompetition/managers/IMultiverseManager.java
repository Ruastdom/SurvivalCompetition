package xiamomc.survivalcompetition.managers;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface IMultiverseManager
{
    /**
     * Create worlds using the given basename
     *
     * @param worldName World basename
     * @return Whether all operations executed successfully.
     */
    boolean createWorlds(String worldName);

    /**
     * Delete all worlds created using the given basename
     *
     * @param worldName World basename
     * @return Whether all operations executed successfully.
     */
    boolean deleteWorlds(String worldName);

    /**
     * Teleport someone to target world.
     *
     * @param player The player
     * @param worldName Target world name
     */
    void tpToWorld(Player player, String worldName);

    /**
     * 获取当前的比赛世界
     * @return 当前的比赛世界
     */
    List<World> getCurrentWorlds();

    //临时
    String getFirstSpawnWorldName();
}
