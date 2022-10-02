package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Managers.ITeamManager;
import xiamomc.survivalcompetition.SCPluginObject;

public class JoinGameCommand extends SCPluginObject implements IPluginCommand
{
    //region IPluginCommand

    @Override
    public String getCommandName()
    {
        return "joinsg";
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public String getHelpMessage()
    {
        return "加入游戏";
    }

    //endregion

    @Resolved
    private ITeamManager itm;

    @Resolved
    private IPlayerListManager manager;

    @Resolved
    private IGameManager igm;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player player))
        {
            return false;
        }

        if (igm.doesGameStart())
        {
            sender.sendMessage(Component.translatable("游戏已经开始，你无法加入！"));
            return false;
        }

        if (manager.isEmpty())
        {
            this.addSchedule(c ->
            {
                if (manager.listAmount() >= 2)
                {
                    igm.startGame();
                }
                else
                {
                    Bukkit.getServer().broadcast(Component.translatable("由于人数不足，本次匹配已停止！"));
                    manager.clear();
                }
            }, 100);
        }
        if (manager.add(player))
        {
            Bukkit.getServer().broadcast(Component.text(sender.getName())
                    .append(Component.translatable("成功加入队列！当前队列等待人数："))
                    .append(Component.text(manager.listAmount())));
        }
        else
        {
            sender.sendMessage(Component.translatable("加入队列失败，您是否已经在队列中？当前等待人数：")
                    .append(Component.text(manager.listAmount())));
        }
        return true;
    }
}
