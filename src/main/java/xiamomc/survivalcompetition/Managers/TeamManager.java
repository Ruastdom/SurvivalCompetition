package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.Console;
import java.util.List;
import java.util.UUID;

public class TeamManager implements ITeamManager {
    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();

    public Team blue;
    public Team red;
    int redScores = 0;
    int blueScores = 0;

    //队伍计分板
    Objective obj = board.registerNewObjective(
            "Points",
            "dummy",
            Component.text(ChatColor.GOLD + "" + ChatColor.BOLD + "各队得分")
    );

    public void addToTeamBlue(String name) {
        getTeamBlue().addEntry(name);
    }

    public void addToTeamRed(String name) {
        getTeamRed().addEntry(name);
    }

    public Team getTeamRed(){
        if (board.getTeam("GAME_RED") == null) {
            this.red = board.registerNewTeam("GAME_RED");
        } else {
            this.red = board.getTeam("GAME_RED");
        }
        return this.red;
    }

    public Team getTeamBlue(){
        if (board.getTeam("GAME_BLUE") == null){
            this.blue = board.registerNewTeam("GAME_BLUE");
        } else {
            this.blue = board.getTeam("GAME_BLUE");
        }
        return this.blue;
    }

    @Override
    public Team getPlayerTeam(String name) {
        Bukkit.getServer().broadcastMessage(name);
        if (getTeamBlue().getEntries().contains(name)) {
            return getTeamBlue();
        } else if (getTeamRed().getEntries().contains(name)) {
            return getTeamRed();
        } else {
            Bukkit.getServer().broadcastMessage("蓝队 getEntries(): " + getTeamBlue().getEntries().toString());
            Bukkit.getServer().broadcastMessage("无法找到玩家所在队伍");
            return null;
        }
    }

    public void distributeToTeams(List<UUID> list) {
        for ( int i = 0; i < list.size(); i++) {
            int a = i % 2;
            Player player = Bukkit.getPlayer(list.get(i));
            player.setScoreboard(board);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score redScore = obj.getScore(ChatColor.RED + "红队");
            Score blueScore = obj.getScore(ChatColor.BLUE + "蓝队");
            redScore.setScore(redScores);
            blueScore.setScore(blueScores);
            if (a == 0){
                addToTeamRed(player.getName());
                player.sendMessage("您已被分配到红队");
            } else {
                addToTeamBlue(player.getName());
                player.sendMessage("您已被分配到蓝队");
            }
        }
        // 以下代码来自 https://www.mcbbs.net/thread-897858-1-1.html
        // 感谢他们对 Scoreboard 和 Team 的精细讲解 awa

        // 设置显示名
        getTeamRed().setDisplayName("红队");
        getTeamBlue().setDisplayName("蓝队");

        // 设置队伍颜色
        getTeamRed().setColor(ChatColor.RED);
        getTeamBlue().setColor(ChatColor.BLUE);

        // 对于自己的队伍进行NameTag显示, 而对其他队伍关闭 -> 制作出类似吃鸡队友的感觉
        // 这里的FOR_OTHER_TEAM表示的意思是只对其他队伍 关闭
        getTeamRed().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        getTeamBlue().setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);

        // 对于自己的队伍开启防碰撞体积, 而对其他队伍开启体积碰撞
        // 这里的FOR_OWN_TEAM表示的意思是只对本队 关闭
        getTeamRed().setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
        getTeamBlue().setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);

        // 设置同队可看见隐身
        getTeamRed().setCanSeeFriendlyInvisibles(true);
        getTeamBlue().setCanSeeFriendlyInvisibles(true);

        // 取消队伤
        getTeamRed().setAllowFriendlyFire(false);
        getTeamBlue().setAllowFriendlyFire(false);

        // 设置前缀
        getTeamRed().setPrefix("§c红队 - ");
        getTeamBlue().setPrefix("§9蓝队 - ");
    }

    @Override
    public void sendTeammatesMessage() {
        Bukkit.getServer().broadcastMessage("§c红队队员：");
        getTeamRed().getEntries().forEach(player -> Bukkit.getServer().broadcastMessage(ChatColor.RED + " - " + player));
        Bukkit.getServer().broadcastMessage("§9蓝队队员：");
        getTeamBlue().getEntries().forEach(player -> Bukkit.getServer().broadcastMessage(ChatColor.BLUE + " - " + player));
    }

    @Override
    public int getPoints(String teamName) {
        if (teamName == "GAME_RED") {
            return redScores;
        } else if (teamName == "GAME_BLUE") {
            return blueScores;
        } else {
            return 0;
        }
    }

    @Override
    public boolean setPoints(String teamName, int point, List<UUID> playerList) {
        if (teamName == "GAME_RED") {
            board.resetScores(ChatColor.RED + "红队");
            redScores = point;
            Score redScore = obj.getScore(ChatColor.RED + "红队");
            redScore.setScore(redScores);
        } else if (teamName == "GAME_BLUE") {
            board.resetScores(ChatColor.BLUE + "蓝队");
            blueScores = point;
            Score redScore = obj.getScore(ChatColor.BLUE + "蓝队");
            redScore.setScore(blueScores);
        } else {
            return false;
        }
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            player.setScoreboard(board);
        }
        return true;
    }

    @Override
    public boolean setPoints(Team team, int point, List<UUID> playerList) {
        if (team.equals(getTeamRed())) {
            board.resetScores(ChatColor.RED + "红队");
            redScores = point;
            Score redScore = obj.getScore(ChatColor.RED + "红队");
            redScore.setScore(redScores);
        } else if (team.equals(getTeamBlue())) {
            board.resetScores(ChatColor.BLUE + "蓝队");
            blueScores = point;
            Score redScore = obj.getScore(ChatColor.BLUE + "蓝队");
            redScore.setScore(blueScores);
        } else {
            return false;
        }
        for (UUID uuid : playerList) {
            Player player = Bukkit.getPlayer(uuid);
            player.setScoreboard(board);
        }
        return true;
    }

    @Override
    public void removeAllPlayersFromTeams() {
        getTeamBlue().getEntries().forEach(player -> getTeamBlue().removeEntry(player));
        getTeamRed().getEntries().forEach(player -> getTeamRed().removeEntry(player));
        getTeamBlue().unregister();
        getTeamRed().unregister();
    }
}
