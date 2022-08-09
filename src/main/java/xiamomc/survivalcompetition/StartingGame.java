package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import xiamomc.survivalcompetition.Managers.*;

public class StartingGame extends PluginObject {
    public StartingGame(){
        noticeGameStarting();
        dayTriggers();
        gameEnding();
    }

    public void noticeGameStarting(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        igm.startGame();
        Bukkit.getServer().broadcastMessage("游戏开始!");
    }

    public void dayTriggers(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        new BukkitRunnable(){
            @Override
            public void run() {
                igm.firstDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 0);
        new BukkitRunnable(){
            @Override
            public void run() {
                igm.secondDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 1500);
        new BukkitRunnable(){
            @Override
            public void run() {
                igm.thirdDayTrigger(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 3000);
    }

    public void gameEnding(){
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        new BukkitRunnable(){
            @Override
            public void run() {
                igm.endGame(ipm.getList());
            }
        }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 4500);
    }
}
