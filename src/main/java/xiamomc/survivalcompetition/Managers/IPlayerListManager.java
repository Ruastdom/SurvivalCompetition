package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;

import java.util.List;

public interface IPlayerListManager
{
    List<Player> getList();

    boolean contains(Player player);

    boolean add(Player player);

    boolean remove(Player player);

    boolean checkExistence();

    boolean isEmpty();

    int listAmount();

    boolean clear();
}
