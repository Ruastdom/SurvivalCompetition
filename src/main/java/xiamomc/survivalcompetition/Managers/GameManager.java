package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.PluginObject;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class GameManager extends PluginObject implements IGameManager {
    final TextComponent titleDay1Sub = Component.text("今天你们不能互相攻击，请好好发展");
    final TextComponent titleDay2Sub = Component.text("你准备好迎接敌方的进攻了吗？");
    final TextComponent titleDay3Sub = Component.text("希望你能给这次竞赛画上圆满的句号 :)");
    boolean isGameStarted;

    long[] times = new long[]{100, 2000, 100};
    @NotNull
    String winner = "";

    @Override
    public boolean startGame() {
        new BukkitRunnable(){
            @Override
            public void run() {
                ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
                IPlayerListManager igm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

                igm.checkExistence();
                itm.distributeToTeams(igm.getList());
                itm.sendTeammatesMessage();
            }
        }.runTask(SurvivalCompetition.instance);
        isGameStarted = true;
        return true;
    }

    @Override
    public void firstDayTrigger(List<UUID> playerList) {
        final TextComponent titleMain = Component.text("第一天");
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay1Sub);
            }
        }
    }

    @Override
    public void secondDayTrigger(List<UUID> playerList) {
        final TextComponent titleMain = Component.text("第二天");
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay2Sub);
            }
        }
    }

    @Override
    public void thirdDayTrigger(List<UUID> playerList) {
        final TextComponent titleMain = Component.text("第三天");
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay3Sub);
            }
        }
    }

    @Override
    public boolean endGame(List<UUID> playerList) {
        final TextComponent titleMain = Component.text("游戏结束");
        new BukkitRunnable(){
            @Override
            public void run() {
                ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
                IPlayerListManager igm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

                int redScore = itm.getPoints(itm.getTeamRed().getName());
                int blueScore = itm.getPoints(itm.getTeamBlue().getName());
                if (redScore > blueScore) {
                    winner = "红队胜利！";
                } else if (redScore < blueScore) {
                    winner = "蓝队胜利！";
                } else {
                    winner = "两队势均力敌";
                }
                final TextComponent titleWinSub = Component.text(winner);
                for (UUID uuid : playerList) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                        player.sendTitlePart(TitlePart.TITLE, titleMain);
                        player.sendTitlePart(TitlePart.SUBTITLE, titleWinSub);
                    }
                }
                igm.clear();
                itm.removeAllPlayersFromTeams();
            }
        }.runTask(SurvivalCompetition.instance);

        isGameStarted = false;
        return true;
    }

    @Override
    public boolean doesGameStart() {
        return isGameStarted;
    }
}