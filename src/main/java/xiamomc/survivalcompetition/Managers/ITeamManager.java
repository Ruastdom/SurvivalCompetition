package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.Nullable;
import xiamomc.survivalcompetition.Misc.TeamInfo;

import java.util.List;
import java.util.UUID;

public interface ITeamManager {

    /**
     * 添加队伍
     * @param ti 队伍信息
     * @return 队伍添加是否成功
     */
    boolean AddTeam(TeamInfo ti);

    /**
     * 添加队伍
     * @param identifier 队伍ID
     * @param name 队伍名称
     * @param desc 队伍描述
     * @return 添加是否成功
     */
    boolean AddTeam(String identifier, String name, String desc);

    /**
     * 添加队伍
     * @param identifier 队伍ID
     * @param name 队伍名称
     * @return 添加是否成功
     */
    boolean AddTeam(String identifier, String name);

    /**
     * 添加队伍
     * @param identifier 队伍ID
     * @return 添加是否成功
     */
    boolean AddTeam(String identifier);

    /**
     * 获取队伍
     * @param identifier 队伍ID
     * @return 获取到的队伍信息
     */
    @Nullable
    TeamInfo GetTeam(String identifier);

    /**
     * 获取所有队伍
     * @return 队伍列表
     */
    List<TeamInfo> GetTeams();

    /**
     * 添加玩家到队伍
     * @param player 目标玩家
     * @param ti 目标队伍的队伍信息
     * @return 添加是否成功
     */
    boolean AddPlayerToTeam(Player player, TeamInfo ti);

    /**
     * 添加玩家到队伍
     * @param player 目标玩家
     * @param identifier 目标队伍的ID
     * @return 添加是否成功
     */
    boolean AddPlayerToTeam(Player player, String identifier);

    /**
     * 从队伍中移除玩家
     * @param player 目标玩家
     * @param ti 目标队伍的队伍信息
     * @return 移除是否成功
     */
    boolean RemovePlayerFromTeam(Player player, TeamInfo ti);

    /**
     * 从队伍中移除玩家
     * @param player 目标玩家
     * @param identifier 目标队伍的ID
     * @return 移除是否成功
     */
    boolean RemovePlayerFromTeam(Player player, String identifier);

    /**
     * 从玩家所在的队伍中移除玩家
     * @param player 目标玩家
     * @return 移除是否成功
     */
    boolean RemovePlayerFromTeam(Player player);

    /**
     * 获取玩家的队伍信息
     * @param player 目标玩家
     * @return 玩家所处队伍的队伍信息
     */
    TeamInfo GetPlayerTeam(Player player);

    void distributeToTeams(List<UUID> list);

    void sendTeammatesMessage();

    int getPoints(String identifier);

    boolean setPoints(String identifier, int point);
    boolean setPoints(TeamInfo teamInfo, int point);

    void removeAllPlayersFromTeams();
}
