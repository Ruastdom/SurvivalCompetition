package xiamomc.survivalcompetition.managers;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public interface IMultiverseManager
{
    boolean createWorlds(String worldName);

    boolean deleteWorlds(String worldName);

    void createSMPWorldGroup(String worldName);

    boolean linkSMPWorlds(String worldName);

    void tpToWorld(Player player, String worldName);

    /**
     * 获取当前的比赛世界
     * @return
     */
    List<World> getCurrentWorlds();

    //临时
    String getFirstSpawnWorldName();
}
