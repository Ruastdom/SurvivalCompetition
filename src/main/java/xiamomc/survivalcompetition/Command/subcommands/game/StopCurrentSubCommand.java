package xiamomc.survivalcompetition.Command.subcommands.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionNode;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;

public class StopCurrentSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "stopcurrent";
    }

    @Override
    public List<String> onTabComplete(List<String> args, CommandSender source)
    {
        return null;
    }

    private final String permNode = PermissionNode.create("stopcurrent").toString();

    @Override
    public String getPermissionRequirement()
    {
        return permNode;
    }

    @Override
    public String getHelpMessage()
    {
        return "停止当前游戏";
    }

    @Resolved
    private IGameManager game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        if (!sender.hasPermission(getPermissionRequirement()))
        {
            sender.sendMessage(Component.translatable("禁止接触").color(NamedTextColor.RED));
            return true;
        }

        if (!game.endGame())
        {
            Logger.warn("未能停止游戏");
            sender.sendMessage(Component.translatable("未能停止游戏。游戏尚未开始？"));
        }

        return true;
    }
}
