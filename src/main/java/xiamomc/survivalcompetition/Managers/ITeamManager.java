package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import xiamomc.survivalcompetition.Misc.TeamInfo;

import java.util.List;

public interface ITeamManager
{

    /**
     * 添加队伍
     *
     * @param ti 队伍信息
     * @return 队伍添加是否成功
     */
    boolean addTeam(TeamInfo ti);

    /**
     * 添加队伍
     *
     * @param identifier 队伍ID
     * @param name       队伍名称
     * @param desc       队伍描述
     * @return 添加是否成功
     */
    boolean addTeam(String identifier, String name, String desc);

    /**
     * 添加队伍
     *
     * @param identifier 队伍ID
     * @param name       队伍名称
     * @return 添加是否成功
     */
    boolean addTeam(String identifier, String name);

    /**
     * 添加队伍
     *
     * @param identifier 队伍ID
     * @return 添加是否成功
     */
    boolean addTeam(String identifier);

    /**
     * 获取队伍
     *
     * @param identifier 队伍ID
     * @return 获取到的队伍信息
     */
    @Nullable
    TeamInfo getTeam(String identifier);

    /**
     * 获取所有队伍
     *
     * @return 队伍列表
     */
    List<TeamInfo> getTeams();

    /**
     * 添加玩家到队伍
     *
     * @param player 目标玩家
     * @param ti     目标队伍的队伍信息
     * @return 添加是否成功
     */
    boolean addPlayerToTeam(Player player, TeamInfo ti);

    /**
     * 添加玩家到队伍
     *
     * @param player     目标玩家
     * @param identifier 目标队伍的ID
     * @return 添加是否成功
     */
    boolean addPlayerToTeam(Player player, String identifier);

    /**
     * 从队伍中移除玩家
     *
     * @param player 目标玩家
     * @param ti     目标队伍的队伍信息
     * @return 移除是否成功
     */
    boolean removePlayerFromTeam(Player player, TeamInfo ti);

    /**
     * 从队伍中移除玩家
     *
     * @param player     目标玩家
     * @param identifier 目标队伍的ID
     * @return 移除是否成功
     */
    boolean removePlayerFromTeam(Player player, String identifier);

    /**
     * 从玩家所在的队伍中移除玩家
     *
     * @param player 目标玩家
     * @return 移除是否成功
     */
    boolean removePlayerFromTeam(Player player);

    /**
     * 获取玩家的队伍信息
     *
     * @param player 目标玩家
     * @return 玩家所处队伍的队伍信息
     */
    TeamInfo getPlayerTeam(Player player);

    /**
     * 将玩家分散到队伍中
     *
     * @param list 用于分散的玩家列表
     */
    void distributeToTeams(List<Player> list);

    /**
     * 向玩家发送队伍成员信息
     */
    void sendTeammatesMessage();

    /**
     * 获取某一队伍的分数
     *
     * @param identifier 队伍ID
     * @return 分数
     */
    int getPoints(String identifier);

    /**
     * 设置某一队伍的分数
     *
     * @param identifier 队伍ID
     * @param point      分数
     * @return 设置是否成功
     */
    boolean setPoints(String identifier, int point);

    /**
     * 设置某一队伍的分数
     *
     * @param teamInfo 目标队伍
     * @param point    分数
     * @return 设置是否成功
     */
    boolean setPoints(TeamInfo teamInfo, int point);

    /**
     * 将所有玩家从自己的队伍中移出
     */
    void removeAllPlayersFromTeams();
}
