package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Misc.Colors;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionNode;
import xiamomc.survivalcompetition.Misc.Permissions.PermissionUtils;
import xiamomc.survivalcompetition.Misc.PluginObject;

public class GMCommand extends PluginObject implements IPluginCommand
{
    @Override
    public String getCommandName()
    {
        return "game";
    }

    @Resolved
    private PluginConfigManager config;

    @Resolved
    private IGameManager game;

    @Resolved
    private IPlayerListManager playerListManager;

    @Resolved
    private PermissionUtils permissions;

    @Initializer
    private void load()
    {
    }

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
