package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Misc.TeamInfo;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.time.Duration;
import java.time.Instant;
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

    String time;
    @Override
    public boolean startGame() {
        ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
        IPlayerListManager igm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);

        igm.checkExistence();
        itm.distributeToTeams(igm.getList());
        itm.sendTeammatesMessage();

        ICareerManager icm = (ICareerManager) Dependencies.Get(ICareerManager.class);
        Bukkit.getServer().broadcastMessage("请选择职业：");
        icm.getCareerList().forEach(career -> Bukkit.getServer().broadcast(career.GetNameAsComponent()));
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

    private TeamInfo drawTeam = new TeamInfo("没有人", "", "DRAW");

    @Override
    public boolean endGame(List<UUID> playerList) {
        final TextComponent titleMain = Component.text("游戏结束");

        ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
        IPlayerListManager igm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);
        IMultiverseManager imm = (IMultiverseManager) Dependencies.Get(IMultiverseManager.class);

        TeamInfo winnerTeam = drawTeam;
        int wTScore = 0;

        for (TeamInfo ti : itm.GetTeams())
        {
            var score = itm.getPoints(ti.Identifier);

            if (score >= 0 && score > wTScore)
            {
                winnerTeam = ti;
                wTScore = score;
            }
        }

        final var titleWinSub = Component.text(winnerTeam.Name).append(Component.translatable("胜出！")).asComponent();
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleWinSub);
                player.resetMaxHealth();
                imm.tpToWorld(Bukkit.getPlayer(uuid).getName(), SurvivalCompetition.getMultiverseCore().getMVWorldManager().getFirstSpawnWorld().getName());
            }
        }
        igm.clear();
        itm.removeAllPlayersFromTeams();

        ICareerManager icm = (ICareerManager) Dependencies.Get(ICareerManager.class);
        icm.clear();

        if (time != null)
            imm.deleteWorlds(time);

        isGameStarted = false;
        return true;
    }

    @Override
    public boolean doesGameStart() {
        return isGameStarted;
    }

    @Override
    public String getNewWorldName() {
        if (!isGameStarted){
            time = String.valueOf(Instant.now().getEpochSecond());
        }
        return time;
    }
}