package xiamomc.survivalcompetition.commands;

import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.messages.FormattableMessage;
import xiamomc.survivalcompetition.commands.subcommands.game.ReloadSubCommand;
import xiamomc.survivalcompetition.commands.subcommands.game.StopCurrentSubCommand;
import xiamomc.survivalcompetition.messages.CommandStrings;

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

    private final List<FormattableMessage> notes = List.of();

    @Override
    public List<FormattableMessage> getNotes()
    {
        return notes;
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return CommandStrings.gmCommandHelpString();
    }
}
