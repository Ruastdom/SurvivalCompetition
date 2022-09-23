package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.Nullable;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Configuration.ConfigNode;
import xiamomc.survivalcompetition.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Misc.TeamInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamManager extends PluginObject implements ITeamManager
{
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getMainScoreboard();

    public static final TeamInfo FallBackTeam = new TeamInfo(
            "无效队伍配置", "FIX ME PLEASE", "FALLBACK", NamedTextColor.AQUA
    );

    private final ConfigNode baseConfigNode = ConfigNode.New().Append("TeamManager");
    private final ConfigNode teamsNode = baseConfigNode.GetCopy().Append("Teams");

    public TeamManager()
    {
    }

    @Resolved
    private PluginConfigManager config;

    @Initializer
    private void init(PluginConfigManager config)
    {
        config.OnConfigRefresh(c -> onConfigUpdate(), true);
    }

    private void onConfigUpdate()
    {
        //获取队伍列表
        var teams = config.Get(ArrayList.class, teamsNode);

        //如果没有队伍，则使用默认配置
        if (teams == null)
        {
            var list = new ArrayList<TeamInfo>(Arrays.asList(
                    new TeamInfo("红队", "红队", "TEAMRED", NamedTextColor.RED),
                    new TeamInfo("蓝队", "蓝队", "TEAMBLUE", NamedTextColor.BLUE)
            ));

            config.Set(teamsNode, list);
            teams = list;
        }

        //添加队伍
        for (var t : teams)
            AddTeam((TeamInfo) t);

        //如果teamMap是空的
        if (teamMap.size() == 0)
            AddTeam(FallBackTeam);

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
    public boolean AddTeam(TeamInfo ti)
    {
        Logger.info("添加队伍：" + ti.Name);

        var teamId = ti.Identifier;
        if (teamId == null || teamId.isEmpty() || teamId.isBlank() || teamId.equals("NULL"))
        {
            Logger.error("无效队伍ID：" + ti.Identifier + "，将不会添加此队伍");
            return false;
        }

        if (teamMap.containsValue(ti)) return false;

        var prevTeam = board.getTeam(ti.Identifier);
        if (prevTeam != null)
            prevTeam.unregister();

        // 以下代码来自 https://www.mcbbs.net/thread-897858-1-1.html
        // 感谢他们对 Scoreboard 和 Team 的精细讲解 awa
        var newTeam = board.registerNewTeam(ti.Identifier);
        newTeam.setAllowFriendlyFire(false);
        newTeam.setCanSeeFriendlyInvisibles(true);
        newTeam.color(ti.TeamColor);
        newTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        newTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        newTeam.displayName(Component.translatable(ti.Name));
        newTeam.prefix(Component.text(ti.Name).append(Component.text(" - ")));

        ti.Team = newTeam;
        teamMap.put(ti.Identifier, ti);
        return true;
    }

    @Override
    public boolean AddTeam(String identifier, String name, String desc)
    {
        return this.AddTeam(new TeamInfo(name, desc, identifier));
    }

    @Override
    public boolean AddTeam(String identifier, String name)
    {
        return this.AddTeam(identifier, name, "");
    }

    @Override
    public boolean AddTeam(String identifier)
    {
        return this.AddTeam(identifier, identifier);
    }

    @Override
    @Nullable
    public TeamInfo GetTeam(String identifier)
    {
        return teamMap.get(identifier);
    }

    @Override
    public List<TeamInfo> GetTeams()
    {
        return teamMap.values().stream().toList();
    }

    @Override
    public boolean AddPlayerToTeam(Player player, TeamInfo ti)
    {
        if (teamMap.containsValue(ti))
        {
            var prevPlayerTeam = GetPlayerTeam(player);

            if (prevPlayerTeam != null)
                teamPlayersMap.get(prevPlayerTeam).remove(player);

            RemovePlayerFromTeam(player, ti);
            teamPlayersMap.get(ti).add(player);
            ti.Team.addEntry(player.getName());
            return true;
        }

        return false;
    }

    @Override
    public boolean AddPlayerToTeam(Player player, String identifier)
    {
        var targetTeam = this.GetTeam(identifier);

        return AddPlayerToTeam(player, targetTeam);
    }

    @Override
    public boolean RemovePlayerFromTeam(Player player, TeamInfo ti)
    {
        if (ti == null || player == null) return false;

        ti.Team.removeEntry(player.getName());
        return teamPlayersMap.get(ti).remove(player);
    }

    @Override
    public boolean RemovePlayerFromTeam(Player player, String identifier)
    {
        var targetTeam = this.GetTeam(identifier);

        return RemovePlayerFromTeam(player, targetTeam);
    }

    @Override
    public boolean RemovePlayerFromTeam(Player player)
    {
        var targetTeam = GetPlayerTeam(player);

        return RemovePlayerFromTeam(player, targetTeam);
    }

    @Override
    public TeamInfo GetPlayerTeam(Player player)
    {
        for (var es : teamPlayersMap.entrySet())
        {
            if (es.getValue().contains(player)) return es.getKey();
        }

        return null;
    }

    private final ConcurrentHashMap<TeamInfo, Score> teamScoreMap = new ConcurrentHashMap<>();

    @Override
    public void distributeToTeams(List<UUID> list)
    {
        //初始化记分板显示
        refreshObjective();
        teamScoreMap.clear();
        teamPlayersMap.clear();

        //对列表随机排序
        Collections.shuffle(list, new Random());

        for (TeamInfo ti : teamMap.values())
        {
            //todo: 在这里显示ti.Name
            //添加分数显示到teamScoreMap
            var score = obj.getScore(ti.Name);

            teamScoreMap.put(ti, score);

            score.setScore(0);

            teamPlayersMap.put(ti, new ArrayList<>());
        }

        var teams = teamMap.values().toArray();

        var targetIndex = -1; //= (int) ((Math.random() * 1000) % teamMap.values().size());

        //分布玩家
        for (UUID uuid : list)
        {
            targetIndex += 1;

            Player player = Bukkit.getPlayer(uuid);
            if (player == null)
            {
                Logger.warn("未能找到与UUID" + uuid + "对应的玩家");
                continue;
            }

            var targetTeam = (TeamInfo) teams[targetIndex % teams.length];
            this.AddPlayerToTeam(player, targetTeam);
            player.sendMessage(Component.text("您已被分配到" + targetTeam.Name));
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
            Bukkit.getServer().broadcast(Component.text(ti.Name)
                    .append(Component.text("成员：", ti.TeamColor)).asComponent());

            Bukkit.getServer().broadcast(Component.text(playerListString.toString()));
        }
    }

    @Override
    public int getPoints(String identifier)
    {
        var score = teamScoreMap.get(GetTeam(identifier));

        if (score != null && score.isScoreSet()) return score.getScore();
        return -1;
    }

    @Override
    public boolean setPoints(String identifier, int point)
    {
        return this.setPoints(GetTeam(identifier), point);
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
                RemovePlayerFromTeam(player, es.getKey());
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
