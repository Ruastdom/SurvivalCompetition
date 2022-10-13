package xiamomc.survivalcompetition.commands.subcommands.game;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.pluginbase.messages.FormattableMessage;
import xiamomc.survivalcompetition.messages.CommandStrings;
import xiamomc.survivalcompetition.misc.permission.PermissionNode;
import xiamomc.survivalcompetition.SCPluginObject;

public class ReloadSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "reload";
    }

    private final String permNode = PermissionNode.create("reload").toString();

    @Override
    public String getPermissionRequirement()
    {
        return permNode;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return CommandStrings.gmReloadCommandHelpString();
    }

    @Resolved
    private PluginConfigManager config;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull String[] args)
    {
        config.reload();
        sender.sendMessage(Component.translatable("刷新成功！"));

        return true;
    }
}
