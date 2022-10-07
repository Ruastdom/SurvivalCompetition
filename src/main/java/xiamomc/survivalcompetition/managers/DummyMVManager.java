package xiamomc.survivalcompetition.managers;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DummyMVManager implements IMultiverseManager
{
    @Override
    public boolean createWorlds(String worldName)
    {
        return true;
    }

    @Override
    public boolean deleteWorlds(String worldName)
    {
        return true;
    }

    @Override
    public void createSMPWorldGroup(String worldName)
    {

    }

    @Override
    public boolean linkSMPWorlds(String worldName)
    {
        return true;
    }

    @Override
    public void tpToWorld(Player player, String worldName)
    {

    }

    private ArrayList<World> emptyWorldList = new ArrayList<>();

    @Override
    public List<World> getCurrentWorlds()
    {
        return emptyWorldList;
    }

    @Override
    public String getFirstSpawnWorldName()
    {
        return null;
    }
}
