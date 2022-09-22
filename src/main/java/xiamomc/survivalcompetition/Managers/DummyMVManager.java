package xiamomc.survivalcompetition.Managers;

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
    public void tpToWorld(String playerName, String worldName)
    {

    }
}
