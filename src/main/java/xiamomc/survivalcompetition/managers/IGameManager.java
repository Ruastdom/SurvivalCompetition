package xiamomc.survivalcompetition.managers;

import java.util.List;
import java.util.UUID;

public interface IGameManager
{
    boolean startGame();

    @Deprecated
    boolean endGame(List<UUID> playerList);

    boolean endGame();

    boolean gameRunning();

    String getNewWorldName();

    boolean DoesAllowCareerSelect();
}
