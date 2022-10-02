package xiamomc.survivalcompetition.Command.subcommands.game;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionNode;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;

public class ReloadSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "reload";
    }

    @Override
    public List<String> onTabComplete(List<String> args, CommandSender source)
    {
        return null;
    }

    private final String permNode = PermissionNode.create("reload").toString();

    @Override
    public String getPermissionRequirement()
    {
        return permNode;
    }

    @Override
    public String getHelpMessage()
    {
        return "重载游戏";
    }

    @Resolved
    private PluginConfigManager config;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        if (!sender.hasPermission(getPermissionRequirement()))
        {
            sender.sendMessage(Component.translatable("禁止接触").color(NamedTextColor.RED));
            return true;
        }

        config.reload();
        sender.sendMessage(Component.translatable("刷新成功！"));

        return true;
    }
}
