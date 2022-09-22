package xiamomc.survivalcompetition;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Managers.ITeamManager;
import xiamomc.survivalcompetition.Misc.PluginObject;
import xiamomc.survivalcompetition.Misc.TeamInfo;

import static org.bukkit.entity.EntityType.ENDER_DRAGON;
import static org.bukkit.entity.EntityType.PLAYER;

public class EventProcessor extends PluginObject implements Listener
{
    @Resolved(ShouldSolveImmediately = true)
    private ITeamManager itm;

    @Resolved(ShouldSolveImmediately = true)
    private IPlayerListManager ipm;

    @Resolved(ShouldSolveImmediately = true)
    private IGameManager igm;

    @EventHandler
    public void DeathHandler(PlayerDeathEvent e)
    {
        if (e.getPlayer().getKiller() == null) return;

        if (igm.doesGameStart() && e.getPlayer().getKiller().getType() == PLAYER)
        {
            Player player = e.getPlayer();
            Player killer = player.getKiller();

            int playerMaxHealth = (int) player.getMaxHealth();
            int killerMaxHealth = (int) killer.getMaxHealth();

            TeamInfo playerTeam = itm.GetPlayerTeam(player);

            itm.setPoints(playerTeam, itm.getPoints(playerTeam.Identifier) - 20);

            player.setMaxHealth((double) playerMaxHealth - 2.0);

            if (player.getKiller() != null)
            {
                TeamInfo killerTeam = itm.GetPlayerTeam(killer);
                itm.setPoints(killerTeam, itm.getPoints(killerTeam.Identifier) + playerMaxHealth);
                killer.setMaxHealth((double) killerMaxHealth + 2.0);
            }
        }
    }

    @EventHandler
    public void EnderDragonDeathEvent(EntityDeathEvent e)
    {

    }

    @EventHandler
    public void EnderDragonHurtEvent(EntityDamageByEntityEvent e)
    {
        if (igm.doesGameStart() && e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == ENDER_DRAGON)
        {
            Player damager = (Player) e.getDamager();
            int damage = (int) e.getDamage();

            TeamInfo playerTeam = itm.GetPlayerTeam(damager);

            itm.setPoints(playerTeam, itm.getPoints(playerTeam.Identifier) + damage);

        }
    }
}
