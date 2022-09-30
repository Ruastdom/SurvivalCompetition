package xiamomc.survivalcompetition.Command;

import org.bukkit.command.CommandExecutor;

public interface IPluginCommand extends CommandExecutor
{
    public String getCommandName();
}
