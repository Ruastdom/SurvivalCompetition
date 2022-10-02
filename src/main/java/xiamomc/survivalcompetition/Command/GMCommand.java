package xiamomc.survivalcompetition.Command;

import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.survivalcompetition.Command.subcommands.game.ReloadSubCommand;
import xiamomc.survivalcompetition.Command.subcommands.game.StopCurrentSubCommand;

import java.util.List;

public class GMCommand extends SCSubCommandHandler
{
    @Override
    public String getCommandName()
    {
        return "game";
    }

    private final List<ISubCommand> subCommands = List.of(
            new ReloadSubCommand(),
            new StopCurrentSubCommand()
    );

    @Override
    public List<ISubCommand> getSubCommands()
    {
        return subCommands;
    }

    private final List<String> notes = List.of("");

    @Override
    public List<String> getNotes()
    {
        return notes;
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public String getHelpMessage()
    {
        return "游戏指令";
    }
}
