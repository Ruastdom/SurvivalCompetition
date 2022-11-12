package xiamomc.survivalcompetition.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.commands.subcommands.career.GenericCareerSubCommand;
import xiamomc.survivalcompetition.managers.ICareerManager;
import xiamomc.survivalcompetition.managers.IGameManager;
import xiamomc.survivalcompetition.messages.CommandStrings;

import java.util.ArrayList;
import java.util.List;

public class CareerCommand extends SCSubCommandHandler
{
    @Resolved
    private IGameManager game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!game.allowCareerSelect())
        {
            sender.sendMessage(Component.translatable("现在还不能选择职业"));
            return true;
        }

        return super.onCommand(sender, command, label, args);
    }

    @Override
    public String getCommandName()
    {
        return "setcareer";
    }

    private final List<ISubCommand> subCommands = new ArrayList<>();

    @Initializer
    private void load(ICareerManager careerManager)
    {
        for (var c : careerManager.getCareerList())
            subCommands.add(new GenericCareerSubCommand(c));
    }

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
        return CommandStrings.careerHelpString();
    }
}
