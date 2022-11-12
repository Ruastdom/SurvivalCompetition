package xiamomc.survivalcompetition.commands.subcommands.game;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.managers.IGameManager;
import xiamomc.survivalcompetition.messages.CommandStrings;
import xiamomc.survivalcompetition.misc.permission.PermissionNode;
import xiamomc.survivalcompetition.SCPluginObject;

public class StopCurrentSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "stopcurrent";
    }

    private final String permNode = PermissionNode.create("stopcurrent").toString();

    @Override
    public String getPermissionRequirement()
    {
        return permNode;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return CommandStrings.gmStopCommandHelpString();
    }

    @Resolved
    private IGameManager game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        if (!game.endGame())
        {
            logger.warn("未能停止游戏");
            sender.sendMessage(Component.translatable("未能停止游戏。游戏尚未开始？"));
        }

        return true;
    }
}
