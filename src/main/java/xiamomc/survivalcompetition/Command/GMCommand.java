package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Annotations.Resolved;
import xiamomc.survivalcompetition.Configuration.PluginConfigManager;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.Managers.IPlayerListManager;
import xiamomc.survivalcompetition.Misc.PluginObject;

public class GMCommand extends PluginObject implements IPluginCommand
{
    @Override
    public String GetCommandName()
    {
        return "game";
    }

    @Resolved
    private PluginConfigManager config;

    @Resolved
    private IGameManager game;

    @Resolved
    private IPlayerListManager playerListManager;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length <= 0) return false;

        switch (args[0])
        {
            case "reloadconfig":
                config.Reload();
                commandSender.sendMessage(Component.translatable("刷新成功！"));
                break;

            case "stopcurrent":
                if (!game.endGame())
                {
                    Logger.warn("未能停止游戏");
                    commandSender.sendMessage(Component.translatable("未能停止游戏。游戏尚未开始？"));
                }
                break;
            default:
                return false;
        }

        return true;
    }
}
