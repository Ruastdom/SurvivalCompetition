package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;

import java.util.List;

public interface IPlayerListManager
{
    List<Player> getList();

    boolean Contains(Player player);

    boolean Add(Player player);

    boolean Remove(Player player);

    boolean checkExistence();

    boolean isEmpty();

    int listAmount();

    boolean clear();
}
