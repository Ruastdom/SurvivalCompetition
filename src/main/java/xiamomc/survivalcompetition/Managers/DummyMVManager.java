package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;

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

    @Override
    public String GetFirstSpawnWorldName()
    {
        return null;
    }
}
