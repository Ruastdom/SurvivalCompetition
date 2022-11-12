package xiamomc.survivalcompetition;

import net.kyori.adventure.text.Component;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.survivalcompetition.commands.SCCommandHelper;
import xiamomc.survivalcompetition.managers.ICareerManager;
import xiamomc.survivalcompetition.managers.IGameManager;
import xiamomc.survivalcompetition.managers.ITeamManager;
import xiamomc.survivalcompetition.misc.TeamInfo;

import static org.bukkit.entity.EntityType.ENDER_DRAGON;
import static org.bukkit.entity.EntityType.PLAYER;

public class EventProcessor extends SCPluginObject implements Listener
{
    @Resolved(shouldSolveImmediately = true)
    private ITeamManager teams;

    @Resolved(shouldSolveImmediately = true)
    private IGameManager game;

    @Resolved(shouldSolveImmediately = true)
    private ICareerManager careerManager;

    @Resolved(shouldSolveImmediately = true)
    private BindableList<Player> players;

    @EventHandler
    public void PlayerJoinHandler(PlayerJoinEvent e)
    {
        var player = e.getPlayer();

        var career = careerManager.getPlayerCareer(player);
        if (career != null) career.resetFor(player);

        if (game.gameRunning())
        {
            var targetOptional = players.stream().findAny();
            targetOptional.ifPresent(target ->
            {
                this.addSchedule(c ->
                {
                    player.teleport(target);

                    player.setGameMode(GameMode.SPECTATOR);
                    player.sendMessage(Component.translatable("游戏已经开始，因此您已被传送至游戏世界。"));
                }, 2); //直接设置游戏模式没有效果
            });
        }
    }

    @Resolved
    private SCCommandHelper commandHelper;

    @EventHandler
    public void onTabComplete(TabCompleteEvent e)
    {
        var result = commandHelper.onTabComplete(e.getBuffer(), e.getSender());
        if (result != null) e.setCompletions(result);
    }

    @EventHandler
    public void PlayerLeaveHandler(PlayerQuitEvent e)
    {
        if (game.gameRunning())
        {
            var player = e.getPlayer();

            if (players.contains(player))
            {
                players.remove(player);

                //如果玩家全部退出，则结束游戏
                if (players.size() == 0) game.endGame();
            }
        }
    }

    @EventHandler
    public void DeathHandler(PlayerDeathEvent e)
    {
        if (e.getPlayer().getKiller() == null) return;

        if (game.gameRunning() && e.getPlayer().getKiller().getType() == PLAYER)
        {
            Player player = e.getPlayer();
            Player killer = player.getKiller();

            int playerMaxHealth = (int) player.getMaxHealth();
            int killerMaxHealth = (int) killer.getMaxHealth();

            TeamInfo playerTeam = teams.getPlayerTeam(player);

            teams.setPoints(playerTeam, teams.getPoints(playerTeam.identifier) - 20);

            player.setMaxHealth((double) playerMaxHealth - 2.0);

            if (player.getKiller() != null)
            {
                TeamInfo killerTeam = teams.getPlayerTeam(killer);
                teams.setPoints(killerTeam, teams.getPoints(killerTeam.identifier) + playerMaxHealth);
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
        if (game.gameRunning() && e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == ENDER_DRAGON)
        {
            Player damager = (Player) e.getDamager();
            int damage = (int) e.getDamage();

            TeamInfo playerTeam = teams.getPlayerTeam(damager);

            teams.setPoints(playerTeam, teams.getPoints(playerTeam.identifier) + damage);
        }
    }
}
