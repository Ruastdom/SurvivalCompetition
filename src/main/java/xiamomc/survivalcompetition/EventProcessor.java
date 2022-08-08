package xiamomc.survivalcompetition;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Managers.ITeamManager;
import xiamomc.survivalcompetition.Managers.PlayerListManager;
import xiamomc.survivalcompetition.Managers.TeamManager;

public class EventProcessor implements Listener {
    @EventHandler
    public void DeathHandler (PlayerDeathEvent e) {
        new BukkitRunnable(){
            @Override
            public void run() {
                ITeamManager itm;
                itm = new TeamManager();
                IPlayerListManager ipm;
                ipm = new PlayerListManager();
                Player player = e.getPlayer();
                Player killer = player.getKiller();
                int playerMaxHealth = (int) player.getMaxHealth();
                int killerMaxHealth = (int) killer.getMaxHealth();
                Team playerTeam = itm.getPlayerTeam(player.getName());
                itm.setPoints(playerTeam, itm.getPoints(playerTeam.getName()) - 20, ipm.getList());
                player.setMaxHealth( (double) playerMaxHealth - 2.0);
                if ( player.getKiller() != null) {
                    Team killerTeam = itm.getPlayerTeam(killer.getName());
                    itm.setPoints(killerTeam, itm.getPoints(killerTeam.getName()) + playerMaxHealth , ipm.getList());
                    killer.setMaxHealth( (double) killerMaxHealth + 2.0 );
                }
            }
        }.runTask(SurvivalCompetition.instance);
    }
}
