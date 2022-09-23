package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Configuration.ConfigNode;
import xiamomc.survivalcompetition.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Misc.Colors;
import xiamomc.survivalcompetition.Misc.StageInfo;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Misc.TeamInfo;
import xiamomc.survivalcompetition.SurvivalCompetition;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class GameManager extends PluginObject implements IGameManager
{
    boolean isGameStarted;

    long[] times = new long[]{100, 2000, 100};

    public String CurrentWorldBaseName;

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
                    imm.tpToWorld(Bukkit.getPlayer(uuid), worldName);
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

    //endregion

    //region Implementation of IGameManager

    @Override
    public boolean startGame()
    {
        noticeGameStarting();
        iplm.checkExistence();

        Bukkit.getServer().broadcast(Component.text("请选择职业："));
        icm.getCareerList().forEach(career -> Bukkit.getServer().broadcast(career.GetNameAsComponent()));
        isGameStarted = true;

        stageIndex = -1;
        ticksRemaining = -1;

        this.AddSchedule(c -> tick());
        return true;
    }

    private final List<StageInfo> stages = new ArrayList<>();

    private int stageIndex = -1;
    private StageInfo currentStage;
    private int ticksRemaining = -1;

    private final ConfigNode baseConfigNode = ConfigNode.New().Append("GameManager");
    private final ConfigNode stagesNode = baseConfigNode.GetCopy().Append("Stages");

    @Resolved
    private PluginConfigManager config;

    @Initializer
    private void init()
    {
        config.OnConfigRefresh(c -> onConfigUpdate(), true);
    }

    private void onConfigUpdate()
    {
        if (this.isGameStarted) endGame(iplm.getList());

        var stages = config.Get(ArrayList.class, stagesNode);

        if (stages == null)
        {
            stages = new ArrayList<>(Arrays.asList(
                    new StageInfo("初始阶段",
                            "第一天", "今天你们不能互相攻击，请好好发展",
                            1500, true, true),
                    new StageInfo("第二天",
                            "第二天", "你准备好迎接敌方的进攻了吗？",
                            1500, false, false),
                    new StageInfo("最后一天",
                            "第三天", "希望你能给这次竞赛画上圆满的句号 :)",
                            1500, false, false)
            ));

            config.Set(stagesNode, stages);
        }

        for (var o : stages)
        {
            var s = (StageInfo) o;

            Logger.info("添加阶段：" + s.Name);
            this.stages.add(s);
        }
    }

    private void tick()
    {
        ticksRemaining -= 1;

        if (ticksRemaining <= 0) switchToNextStage();

        if (isGameStarted) this.AddSchedule(c -> tick());
    }

    private void switchToNextStage()
    {
        stageIndex += 1;
        if (stageIndex >= stages.size())
        {
            endGame(iplm.getList());
            return;
        }

        switchToStage(stages.get(stageIndex));
    }

    private void switchToStage(StageInfo si)
    {
        var list = iplm.getList();
        ticksRemaining = si.Lasts;
        currentStage = si;
        Logger.info("切换到" + si.Name);

        for (UUID uuid : list)
        {
            var player = Bukkit.getPlayer(uuid);
            if (player != null)
            {
                player.sendTitlePart(TitlePart.TIMES, Title.Times.times(Duration.ofMillis(times[0]), Duration.ofMillis(times[1]), Duration.ofMillis(times[2])));
                player.sendTitlePart(TitlePart.SUBTITLE, Component.translatable(si.TitleSub));
                player.sendTitlePart(TitlePart.TITLE, Component.translatable(si.TitleMain));
            }
        }

        if (si.RefreshTeams)
        {
            itm.distributeToTeams(list);
            itm.sendTeammatesMessage();
        }

        if (si.SpreadsPlayer)
        {
            Logger.warn("未实现扩散玩家！");
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

                this.AddSchedule(c -> imm.tpToWorld(player, SurvivalCompetition.getMultiverseCore().getMVWorldManager().getFirstSpawnWorld().getName()), 200);
            }
        }
        iplm.clear();
        itm.removeAllPlayersFromTeams();

        icm.clear();

        if (CurrentWorldBaseName != null)
            this.AddSchedule(c -> imm.deleteWorlds(CurrentWorldBaseName), 250);

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
        return CurrentWorldBaseName = String.valueOf(Instant.now().getEpochSecond());
    }

    //endregion
}