package xiamomc.survivalcompetition.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Bindables.BindableList;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.SCPluginObject;
import xiamomc.survivalcompetition.managers.IGameManager;
import xiamomc.survivalcompetition.messages.CommandStrings;

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
    public FormattableMessage getHelpMessage()
    {
        return CommandStrings.joinGameCommandHelpString();
    }

    //endregion

    @Resolved
    private BindableList<Player> players;

    @Resolved
    private IGameManager igm;

    @Initializer
    private void load()
    {
        this.addSchedule(c -> update());
    }

    private void update()
    {
        countDown--;

        if (countDown == 0)
        {
            countDown = -1;

            if (players.size() >= 2)
                igm.startGame();
            else
            {
                Bukkit.getServer().broadcast(Component.translatable("由于人数不足，本次匹配已停止！"));
                players.clear();
            }
        }

        this.addSchedule(c -> update());
    }

    private int countDown;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!(sender instanceof Player player))
            return false;

        if (igm.gameRunning())
        {
            sender.sendMessage(Component.translatable("游戏已经开始，你无法加入！"));
            return false;
        }

        if (!players.contains(player) && players.add(player))
        {
            Bukkit.getServer().broadcast(Component.text(sender.getName())
                    .append(Component.translatable("成功加入队列！当前队列等待人数："))
                    .append(Component.text(players.size())));

            //Reset countdown time
            countDown = 101;
        }
        else
        {
            sender.sendMessage(Component.translatable("加入队列失败，您是否已经在队列中？当前等待人数：")
                    .append(Component.text(players.size())));
        }
        return true;
    }
}
