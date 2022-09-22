package xiamomc.survivalcompetition.Managers;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerListManager implements IPlayerListManager
{
    private static final List<UUID> PlayerList = new ArrayList<>();

    @Override
    public List<UUID> getList()
    {
        return PlayerList;
    }

    @Override
    public boolean addPlayer(UUID uuid)
    {
        if (!PlayerList.contains(uuid))
        {
            PlayerList.add(uuid);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean removePlayer(UUID uuid)
    {
        return PlayerList.remove(uuid);
    }

    @Override
    public boolean checkExistence()
    {
        PlayerList.removeIf(uuid -> (Bukkit.getPlayer(uuid) == null));
        return true;
    }

    @Override
    public boolean isEmpty()
    {
        return PlayerList.isEmpty();
    }

    @Override
    public int listAmount()
    {
        return PlayerList.size();
    }

    @Override
    public boolean clear()
    {
        PlayerList.clear();
        return true;
    }
}