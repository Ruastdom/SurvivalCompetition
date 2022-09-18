package xiamomc.survivalcompetition;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;
import xiamomc.survivalcompetition.Exceptions.NullDependencyException;
import xiamomc.survivalcompetition.Managers.*;

import static org.bukkit.entity.EntityType.ENDER_DRAGON;
import static org.bukkit.entity.EntityType.PLAYER;

public class EventProcessor extends PluginObject implements Listener {
    @EventHandler
    public void DeathHandler (PlayerDeathEvent e) {
        ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);

        if (igm.doesGameStart() && e.getPlayer().getKiller().getType() == PLAYER) {
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

    @EventHandler
    public void EnderDragonDeathEvent (EntityDeathEvent e){

    }
    @EventHandler
    public void EnderDragonHurtEvent (EntityDamageByEntityEvent e){
        ITeamManager itm = (ITeamManager) Dependencies.Get(ITeamManager.class);
        IPlayerListManager ipm = (IPlayerListManager) Dependencies.Get(IPlayerListManager.class);
        IGameManager igm = (IGameManager) Dependencies.Get(IGameManager.class);

        if (igm.doesGameStart() && e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == ENDER_DRAGON) {
            Player damager = (Player) e.getDamager();
            int damage = (int) e.getDamage();

            Team playerTeam = itm.getPlayerTeam(damager.getName());

            itm.setPoints(playerTeam, itm.getPoints(playerTeam.getName()) + damage, ipm.getList());

        }
    }
}
