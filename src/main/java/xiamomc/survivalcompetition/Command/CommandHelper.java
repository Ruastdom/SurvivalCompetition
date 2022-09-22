package xiamomc.survivalcompetition.Command;

import org.bukkit.Bukkit;
import xiamomc.survivalcompetition.Misc.PluginObject;

import java.util.Objects;

public class CommandHelper extends PluginObject
{
    public CommandHelper()
    {
        this.AddSchedule(c -> initializeCommands());
    }

    private void initializeCommands()
    {
        this.RegisterCommand(new JoinGameCommand());
        this.RegisterCommand(new CareerCommand());
    }

    public boolean RegisterCommand(IPluginCommand command)
    {
        if (Objects.equals(command.GetCommandName(), ""))
            return false;

        var cmd = Bukkit.getPluginCommand(command.GetCommandName());
        if (cmd != null && cmd.getExecutor() == Plugin)
        {
            cmd.setExecutor(command);
            return true;
        }
        else
            return false;
    }
}
