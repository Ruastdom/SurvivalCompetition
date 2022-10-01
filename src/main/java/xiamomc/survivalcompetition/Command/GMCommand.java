package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.pluginbase.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Misc.Colors;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionNode;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionUtils;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;

public class GMCommand extends SCPluginObject implements IPluginCommand
{
    @Override
    public String getCommandName()
    {
        return "game";
    }

    @Override
    public List<String> onTabComplete(String baseName, String[] args, CommandSender source)
    {
        return null;
    }

    @Resolved
    private PluginConfigManager config;

    @Resolved
    private IGameManager game;

    @Resolved
    private IPlayerListManager playerListManager;

    @Resolved
    private PermissionUtils permissions;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length <= 0) return false;

        switch (args[0])
        {
            case "reload":
                if (permissions.hasPermission(sender, PermissionNode.create("reload")))
                {
                    config.reload();
                    sender.sendMessage(Component.translatable("刷新成功！"));
                }
                else
                    sender.sendMessage(Component.translatable("禁止接触", Colors.Red));

                break;

            case "stopcurrent":
                if (permissions.hasPermission(sender, PermissionNode.create("stopcurrent")))
                {
                    if (!game.endGame())
                    {
                        Logger.warn("未能停止游戏");
                        sender.sendMessage(Component.translatable("未能停止游戏。游戏尚未开始？"));
                    }
                }
                else
                    sender.sendMessage(Component.translatable("禁止接触", Colors.Red));

                break;
            default:
                return false;
        }

        return true;
    }
}
