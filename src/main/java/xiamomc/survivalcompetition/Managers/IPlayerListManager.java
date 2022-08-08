package xiamomc.survivalcompetition.Managers;

import java.util.List;
import java.util.UUID;

public interface IPlayerListManager {
    List<UUID> getList();

    boolean addPlayer(UUID uuid);

    boolean removePlayer(UUID uuid);

    boolean checkExistence();

    boolean isEmpty();

    int listAmount();

    boolean clear();
}
