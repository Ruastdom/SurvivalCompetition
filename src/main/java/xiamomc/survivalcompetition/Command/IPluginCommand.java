package xiamomc.survivalcompetition.Command;

import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public interface IPluginCommand extends CommandExecutor
{
    public String GetCommandName();
}
