package xiamomc.survivalcompetition.Managers;

import java.util.List;
import java.util.UUID;

public interface IGameManager
{
    boolean startGame();

    @Deprecated
    boolean endGame(List<UUID> playerList);

    boolean endGame();

    boolean doesGameStart();

    String getNewWorldName();

    boolean DoesAllowCareerSelect();
}
