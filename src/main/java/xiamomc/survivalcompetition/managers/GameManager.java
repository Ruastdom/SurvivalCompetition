package xiamomc.survivalcompetition.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.misc.Colors;
import xiamomc.survivalcompetition.misc.StageInfo;
import xiamomc.survivalcompetition.misc.TeamInfo;
import xiamomc.survivalcompetition.SCPluginObject;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class GameManager extends SCPluginObject implements IGameManager
{
    boolean isGameStarted;

    Duration[] times = new Duration[]
            {
                    Duration.ofMillis(100),
                    Duration.ofMillis(2000),
                    Duration.ofMillis(100)
            };

    public String CurrentWorldBaseName;

    @Resolved
    private ITeamManager itm;

    @Resolved
    private IPlayerListManager players;

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
            this.addSchedule(c ->
            {
                for (var player : players.getPlayers())
                {
                    imm.tpToWorld(player, worldName);
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

        this.addSchedule(c -> generateNewWorld());
    }

    //endregion

    //region Implementation of IGameManager

    private final List<StageInfo> configuredStages = new ArrayList<>();
    private final Stack<StageInfo> stageInfoStack = new Stack<>();

    private final StageInfo endingStage = new StageInfo("胜出", "游戏结束", "", 200, false, false, false);

    private StageInfo currentStage = null;
    private int ticksRemaining = -1;

    private final ConfigNode baseConfigNode = ConfigNode.create().Append("GameManager");
    private final ConfigNode stagesNode = baseConfigNode.getCopy().Append("Stages");

    @Resolved
    private PluginConfigManager config;

    @Initializer
    private void init()
    {
        config.onConfigRefresh(c -> onConfigUpdate(), true);
    }

    @Override
    public boolean startGame()
    {
        if (isGameStarted) return false;

        logger.warn("START");
        noticeGameStarting();
        players.removeAllOffline();

        isGameStarted = true;

        ticksRemaining = -1;
        currentStage = null;

        stageInfoStack.clear();

        for (var si : configuredStages)
            stageInfoStack.add(0, si);

        this.addSchedule(c -> tick());
        return true;
    }

    private void onConfigUpdate()
    {
        if (this.isGameStarted) endGame();

        var stages = config.get(ArrayList.class, stagesNode);

        if (stages != null) stages.removeIf(Objects::isNull);

        if (stages == null || stages.size() == 0)
        {
            stages = new ArrayList<>(Arrays.asList(
                    new StageInfo("初始阶段",
                            "第一天", "今天你们不能互相攻击，请好好发展",
                            1500, true, true, true),
                    new StageInfo("第二天",
                            "第二天", "你准备好迎接敌方的进攻了吗？",
                            1500, false, false, false),
                    new StageInfo("最后一天",
                            "第三天", "希望你能给这次竞赛画上圆满的句号 :)",
                            1500, false, false, false)
            ));

            config.set(stagesNode, stages);
        }

        for (var o : stages)
        {
            var s = (StageInfo) o;

            logger.info("添加阶段：" + s.name);
            if (!configuredStages.contains(s))
                this.configuredStages.add(s);
        }
    }

    private void tick()
    {
        ticksRemaining -= 1;

        if (ticksRemaining <= 0) switchToNextStage();

        if (isGameStarted) this.addSchedule(c -> tick());
    }

    private void switchToNextStage()
    {
        if (stageInfoStack.isEmpty())
        {
            endGame();
            return;
        }

        switchToStage(stageInfoStack.pop());
    }

    private void switchToStage(StageInfo si)
    {
        var list = players.getPlayers();
        ticksRemaining = si.lasts;
        currentStage = si;
        logger.info("切换到" + si.name);

        for (var player : list)
        {
            player.sendTitlePart(TitlePart.TIMES, Title.Times.times(times[0], times[1], times[2]));
            player.sendTitlePart(TitlePart.SUBTITLE, Component.translatable(si.titleSub));
            player.sendTitlePart(TitlePart.TITLE, Component.translatable(si.titleMain));
        }

        if (si.refreshTeams)
        {
            itm.distributeToTeams(list);
            itm.sendTeammatesMessage();
        }

        if (si.spreadsPlayer)
            logger.warn("未实现扩散玩家！");

        if (si.AllowCareerSelect)
        {
            Bukkit.getServer().broadcast(Component.text("请选择职业："));
            icm.getCareerList().forEach(career -> Bukkit.getServer().broadcast(career.getNameAsComponent()));
        }

        for (var w : imm.getCurrentWorlds())
            w.setGameRule(GameRule.KEEP_INVENTORY, si.allowKeepInventory);
    }

    private final TeamInfo drawTeam = new TeamInfo("没有人", "", "DRAW");

    @Override
    public boolean endGame(List<UUID> playerList)
    {
        return this.endGame();
    }

    @Override
    public boolean endGame()
    {
        if (!isGameStarted) return false;

        TeamInfo winnerTeam = drawTeam;
        int wTScore = 0;

        for (TeamInfo ti : itm.getTeams())
        {
            var score = itm.getPoints(ti.identifier);

            if (score >= 0 && score > wTScore)
            {
                winnerTeam = ti;
                wTScore = score;
            }
        }

        endingStage.titleSub = winnerTeam.name + "胜出！";

        this.switchToStage(endingStage);

        for (var player : players.getPlayers())
        {
            this.addSchedule(c -> imm.tpToWorld(player, imm.getFirstSpawnWorldName()), 200);
        }

        players.clear();
        itm.removeAllPlayersFromTeams();

        icm.clear();

        if (CurrentWorldBaseName != null)
            this.addSchedule(c -> imm.deleteWorlds(CurrentWorldBaseName), 250);

        isGameStarted = false;
        return true;
    }

    @Override
    public boolean gameRunning()
    {
        return isGameStarted;
    }

    @Override
    public String getNewWorldName()
    {
        return CurrentWorldBaseName = String.valueOf(Instant.now().getEpochSecond());
    }

    @Override
    public boolean allowCareerSelect()
    {
        return isGameStarted && currentStage.AllowCareerSelect;
    }

    //endregion
}