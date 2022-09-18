package xiamomc.survivalcompetition.Managers;

import java.util.List;
import java.util.UUID;

public interface IGameManager {
    boolean startGame();

    void firstDayTrigger(List<UUID> playerList);

    void secondDayTrigger(List<UUID> playerList);

    void thirdDayTrigger(List<UUID> playerList);

    boolean endGame(List<UUID> playerList);

    boolean doesGameStart();
    String getNewWorldName();
}
