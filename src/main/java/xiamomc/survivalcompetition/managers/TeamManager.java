package xiamomc.survivalcompetition.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.Nullable;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Configuration.ConfigNode;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.SCPluginObject;
import xiamomc.survivalcompetition.misc.TeamInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager extends SCPluginObject implements ITeamManager
{
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getMainScoreboard();

    public static final TeamInfo FallBackTeam = new TeamInfo(
            "无效队伍配置", "FIX CONFIG PLEASE", "FIX ME PLEASE", "FALLBACK", NamedTextColor.AQUA
    );

    private final ConfigNode baseConfigNode = ConfigNode.create().Append("TeamManager");
    private final ConfigNode teamsNode = baseConfigNode.getCopy().Append("Teams");

    public TeamManager()
    {
    }

    @Resolved
    private PluginConfigManager config;

    @Initializer
    private void init(PluginConfigManager config)
    {
        config.onConfigRefresh(c -> onConfigUpdate(), true);
    }

    private void onConfigUpdate()
    {
        //获取队伍列表
        var teams = config.get(ArrayList.class, teamsNode);

        //移除所有null值
        //这样和下面搭配可以在Teams节点为'[]'或全是null时重置这段设置
        if (teams != null) teams.removeIf(Objects::isNull);

        //如果没有队伍，则使用默认配置
        if (teams == null || teams.size() == 0)
        {
            var list = new ArrayList<TeamInfo>(Arrays.asList(
                    new TeamInfo("红队", "红队 - ", "红队", "TEAMRED", NamedTextColor.RED),
                    new TeamInfo("蓝队", "蓝队 - ", "蓝队", "TEAMBLUE", NamedTextColor.BLUE)
            ));

            config.set(teamsNode, list);
            teams = list;
        }

        //添加队伍
        for (var t : teams)
            addTeam((TeamInfo) t);
    }

    //队伍计分板
    Objective obj;

    private void refreshObjective()
    {
        if (obj != null)
        {
            obj.unregister();
            obj = null;
        }

        obj = board.registerNewObjective
                (
                        "Points" + Math.random(),
                        "dummy",
                        Component.text(ChatColor.GOLD + "" + ChatColor.BOLD + "各队得分")
                );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    //region Implementation of ITeamManager

    private final ConcurrentHashMap<String, TeamInfo> teamMap = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<TeamInfo, List<Player>> teamPlayersMap = new ConcurrentHashMap<>();

    @Override
    public boolean addTeam(TeamInfo ti)
    {
        if (ti == null) throw new IllegalArgumentException("队伍信息不能是null");

        logger.info("添加队伍：" + ti.name);

        var teamId = ti.identifier;
        if (teamId == null || teamId.isEmpty() || teamId.isBlank() || teamId.equals("NULL"))
        {
            logger.error("无效队伍ID：" + ti.identifier + "，将不会添加此队伍");
            return false;
        }

        if (teamMap.containsValue(ti)) return false;

        var prevTeam = board.getTeam(ti.identifier);
        if (prevTeam != null)
            prevTeam.unregister();

        // 以下代码来自 https://www.mcbbs.net/thread-897858-1-1.html
        // 感谢他们对 Scoreboard 和 Team 的精细讲解 awa
        var newTeam = board.registerNewTeam(ti.identifier);
        newTeam.setAllowFriendlyFire(false);
        newTeam.setCanSeeFriendlyInvisibles(true);
        newTeam.color(ti.teamColor);
        newTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        newTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        newTeam.displayName(Component.translatable(ti.name));
        newTeam.prefix(Component.text(ti.teamPrefix).color(ti.teamColor));

        ti.team = newTeam;
        teamMap.put(ti.identifier, ti);
        return true;
    }

    @Override
    public boolean addTeam(String identifier, String name, String desc)
    {
        return this.addTeam(new TeamInfo(name, desc, identifier));
    }

    @Override
    public boolean addTeam(String identifier, String name)
    {
        return this.addTeam(identifier, name, "");
    }

    @Override
    public boolean addTeam(String identifier)
    {
        return this.addTeam(identifier, identifier);
    }

    @Override
    @Nullable
    public TeamInfo getTeam(String identifier)
    {
        return teamMap.get(identifier);
    }

    @Override
    public List<TeamInfo> getTeams()
    {
        return teamMap.values().stream().toList();
    }

    @Override
    public boolean addPlayerToTeam(Player player, TeamInfo ti)
    {
        if (teamMap.containsValue(ti))
        {
            var prevPlayerTeam = getPlayerTeam(player);

            if (prevPlayerTeam != null)
                teamPlayersMap.get(prevPlayerTeam).remove(player);

            removePlayerFromTeam(player, ti);
            teamPlayersMap.get(ti).add(player);
            ti.team.addEntry(player.getName());
            return true;
        }

        return false;
    }

    @Override
    public boolean addPlayerToTeam(Player player, String identifier)
    {
        var targetTeam = this.getTeam(identifier);

        return addPlayerToTeam(player, targetTeam);
    }

    @Override
    public boolean removePlayerFromTeam(Player player, TeamInfo ti)
    {
        if (ti == null || player == null) return false;

        ti.team.removeEntry(player.getName());
        return teamPlayersMap.get(ti).remove(player);
    }

    @Override
    public boolean removePlayerFromTeam(Player player, String identifier)
    {
        var targetTeam = this.getTeam(identifier);

        return removePlayerFromTeam(player, targetTeam);
    }

    @Override
    public boolean removePlayerFromTeam(Player player)
    {
        var targetTeam = getPlayerTeam(player);

        return removePlayerFromTeam(player, targetTeam);
    }

    @Override
    public TeamInfo getPlayerTeam(Player player)
    {
        for (var es : teamPlayersMap.entrySet())
        {
            if (es.getValue().contains(player)) return es.getKey();
        }

        return null;
    }

    private final ConcurrentHashMap<TeamInfo, Score> teamScoreMap = new ConcurrentHashMap<>();

    @Override
    public void distributeToTeams(List<Player> list)
    {
        //初始化记分板显示
        refreshObjective();
        teamScoreMap.clear();
        teamPlayersMap.clear();

        //对列表随机排序
        Collections.shuffle(list, new Random());

        for (TeamInfo ti : teamMap.values())
        {
            //todo: 在这里显示ti.name
            //添加分数显示到teamScoreMap
            var score = obj.getScore(ti.name);

            teamScoreMap.put(ti, score);

            score.setScore(0);

            teamPlayersMap.put(ti, new ArrayList<>());
        }

        var teams = teamMap.values().toArray();

        var targetIndex = -1; //= (int) ((Math.random() * 1000) % teamMap.values().size());

        //分布玩家
        for (var player : list)
        {
            targetIndex += 1;

            var targetTeam = (TeamInfo) teams[targetIndex % teams.length];
            this.addPlayerToTeam(player, targetTeam);
            player.sendMessage(Component.text("您已被分配到" + targetTeam.name));
        }
    }

    @Override
    public void sendTeammatesMessage()
    {
        //列出所有可用队伍信息
        for (var es : teamPlayersMap.entrySet())
        {
            //下一个
            TeamInfo ti = es.getKey();

            //初始化builder
            StringBuilder playerListString = new StringBuilder();

            //将玩家名添加到builder
            for (Player pl : es.getValue())
            {
                playerListString.append(pl.getName()).append("\n");
            }

            //广播成员信息
            Bukkit.getServer().broadcast(Component.text(ti.name)
                    .append(Component.text("成员：")).asComponent().color(ti.teamColor));

            Bukkit.getServer().broadcast(Component.text(playerListString.toString(), ti.teamColor));
        }
    }

    @Override
    public int getPoints(String identifier)
    {
        var score = teamScoreMap.get(getTeam(identifier));

        try
        {
            if (score != null) return score.getScore();
        }
        catch (Exception e)
        {
            logger.warn("无法获取" + identifier + "的分数：" + e.getMessage());
            e.printStackTrace();
        }

        return -1;
    }

    @Override
    public boolean setPoints(String identifier, int point)
    {
        return this.setPoints(getTeam(identifier), point);
    }

    @Override
    public boolean setPoints(TeamInfo ti, int point)
    {
        if (ti == null) return false;

        var score = teamScoreMap.get(ti);

        if (score == null) return false;

        score.setScore(point);

        return true;
    }

    @Override
    public void removeAllPlayersFromTeams()
    {
        for (var es : teamPlayersMap.entrySet())
        {
            //copy list

            var players = new ArrayList<Player>(es.getValue());

            for (Player player : players)
            {
                removePlayerFromTeam(player, es.getKey());
            }
        }

        //todo: 记分板处理应该放在别的地方执行
        if (obj != null)
        {
            obj.unregister();
            obj = null;
        }
    }
    //endregion Implementation of ITeamManager
}
