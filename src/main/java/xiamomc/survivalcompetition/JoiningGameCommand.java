package xiamomc.survivalcompetition;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Managers.*;

public class JoiningGameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        IPlayerListManager manager;
        manager = new PlayerListManager();
        ITeamManager teamManager;
        teamManager = new TeamManager();
        IGameManager igm;
        igm = new GameManager();
        if (!(sender instanceof Player player)) {
            return false;
        }
        if(igm.doesGameStart()){
            sender.sendMessage("游戏已经开始，你无法加入！");
            return false;
        }
        if (manager.isEmpty()) {
            new BukkitRunnable() {
                public void run() {
                    if ( manager.listAmount() >= 2){
                        new StartingGame();
                    } else {
                        Bukkit.getServer().broadcastMessage("由于人数不足，本次匹配已停止！");
                        manager.clear();
                    }
                }
            }.runTaskLaterAsynchronously(SurvivalCompetition.instance, 300);
        }
        if (manager.addPlayer(player.getUniqueId())) {
            Bukkit.getServer().broadcastMessage(sender.getName() + " 成功加入队列！当前队列等待人数 " + manager.listAmount());
        } else {
            sender.sendMessage("加入队列失败，您是否已经在队列中？当前等待人数" + manager.listAmount());
        }
        return true;
    }
}
