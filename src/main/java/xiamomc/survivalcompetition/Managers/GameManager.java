package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Misc.Colors;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Misc.TeamInfo;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GameManager extends PluginObject implements IGameManager
{
    final TextComponent titleDay1Sub = Component.text("今天你们不能互相攻击，请好好发展");
    final TextComponent titleDay2Sub = Component.text("你准备好迎接敌方的进攻了吗？");
    final TextComponent titleDay3Sub = Component.text("希望你能给这次竞赛画上圆满的句号 :)");
    boolean isGameStarted;

    long[] times = new long[]{100, 2000, 100};

    private String currentWorldBaseName;

    @Resolved
    private ITeamManager itm;

    @Resolved
    private IPlayerListManager iplm;

    @Resolved
    private ICareerManager icm;

    @Resolved
    private IMultiverseManager imm;

    //region From StartingGame

    public boolean generateNewWorld()
    {
        String worldName = this.getNewWorldName();

        if (imm.createWorlds(worldName))
        {
            imm.createSMPWorldGroup(worldName);
            imm.linkSMPWorlds(worldName);
            Bukkit.getServer().broadcast(Component.translatable("新的比赛世界已生成，正在传送玩家到新世界......", Colors.Green));
            this.AddSchedule(c ->
            {
                for (UUID uuid : iplm.getList())
                {
                    imm.tpToWorld(Bukkit.getPlayer(uuid).getName(), worldName);
                }
            });
        }
        else
        {
            Bukkit.getServer().broadcast(Component.translatable("世界生成出错！请联系管理员检查 log", Colors.Red));
            return false;
        }

        return true;
    }

    public void noticeGameStarting()
    {
        Bukkit.getServer().broadcast(Component.translatable("正在生成新的世界......", Colors.Red));

        this.AddSchedule(c -> generateNewWorld());
    }

    public void dayTriggers()
    {
        this.AddSchedule(c -> this.firstDayTrigger(iplm.getList()));
        this.AddSchedule(c -> this.secondDayTrigger(iplm.getList()), 1500);
        this.AddSchedule(c -> this.thirdDayTrigger(iplm.getList()), 3000);
        this.AddSchedule(c -> this.endGame(iplm.getList()), 4500);
    }

    //endregion

    //region Implementation of IGameManager

    @Override
    public boolean startGame()
    {
        noticeGameStarting();
        dayTriggers();

        iplm.checkExistence();
        itm.distributeToTeams(iplm.getList());
        itm.sendTeammatesMessage();

        Bukkit.getServer().broadcast(Component.text("请选择职业："));
        icm.getCareerList().forEach(career -> Bukkit.getServer().broadcast(career.GetNameAsComponent()));
        isGameStarted = true;
        return true;
    }

    @Override
    public void firstDayTrigger(List<UUID> playerList)
    {
        final TextComponent titleMain = Component.text("第一天");
        for (UUID uuid : playerList)
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay1Sub);
            }
        }
    }

    @Override
    public void secondDayTrigger(List<UUID> playerList)
    {
        final TextComponent titleMain = Component.text("第二天");
        for (UUID uuid : playerList)
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay2Sub);
            }
        }
    }

    @Override
    public void thirdDayTrigger(List<UUID> playerList)
    {
        final TextComponent titleMain = Component.text("第三天");
        for (UUID uuid : playerList)
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleDay3Sub);
            }
        }
    }

    private final TeamInfo drawTeam = new TeamInfo("没有人", "", "DRAW");

    @Override
    public boolean endGame(List<UUID> playerList)
    {
        if (!isGameStarted) return false;

        final TextComponent titleMain = Component.text("游戏结束");

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
        for (UUID uuid : playerList)
        {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.TITLE, titleMain);
                player.sendTitlePart(TitlePart.SUBTITLE, titleWinSub);
                player.resetMaxHealth();
                imm.tpToWorld(Bukkit.getPlayer(uuid).getName(), SurvivalCompetition.getMultiverseCore().getMVWorldManager().getFirstSpawnWorld().getName());
            }
        }
        iplm.clear();
        itm.removeAllPlayersFromTeams();

        icm.clear();

        if (currentWorldBaseName != null)
            this.AddSchedule(c -> imm.deleteWorlds(currentWorldBaseName), 250);

        isGameStarted = false;
        return true;
    }

    @Override
    public boolean doesGameStart()
    {
        return isGameStarted;
    }

    @Override
    public String getNewWorldName()
    {
        return currentWorldBaseName = String.valueOf(Instant.now().getEpochSecond());
    }

    //endregion
}