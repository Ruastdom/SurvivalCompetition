package xiamomc.survivalcompetition.Managers;

import java.util.List;
import java.util.UUID;

public interface IGameManager
{
    boolean startGame();

    boolean endGame(List<UUID> playerList);

    boolean doesGameStart();

    String getNewWorldName();

    boolean DoesAllowCareerSelect();
}
