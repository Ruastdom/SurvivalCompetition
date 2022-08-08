package xiamomc.survivalcompetition.Managers;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ITeamManager {
    void addToTeamBlue(String name);

    void addToTeamRed(String name);

    Team getTeamRed();

    Team getTeamBlue();

    Team getPlayerTeam(String name);

    void distributeToTeams(List<UUID> list);

    void sendTeammatesMessage();

    int getPoints(String teamName);

    boolean setPoints(String teamName, int point, List<UUID> playerList);
    boolean setPoints(Team team, int point, List<UUID> playerList);

    void removeAllPlayersFromTeams();
}
