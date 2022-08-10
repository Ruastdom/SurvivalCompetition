package xiamomc.survivalcompetition;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.*;

public class EventProcessor extends PluginObject implements Listener {
    @EventHandler
    public void DeathHandler (PlayerDeathEvent e) {
        ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);

        if (igm.doesGameStart()) {
            Player player = e.getPlayer();
            Player killer = player.getKiller();

            int playerMaxHealth = (int) player.getMaxHealth();
            int killerMaxHealth = (int) killer.getMaxHealth();

            Team playerTeam = itm.getPlayerTeam(player.getName());

            itm.setPoints(playerTeam, itm.getPoints(playerTeam.getName()) - 20, ipm.getList());

            player.setMaxHealth((double) playerMaxHealth - 2.0);

            if (player.getKiller() != null) {
                Team killerTeam = itm.getPlayerTeam(killer.getName());
                itm.setPoints(killerTeam, itm.getPoints(killerTeam.getName()) + playerMaxHealth, ipm.getList());
                killer.setMaxHealth((double) killerMaxHealth + 2.0);
            }
        }
    }
}
