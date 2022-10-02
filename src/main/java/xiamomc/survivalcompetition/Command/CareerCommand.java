package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.survivalcompetition.Command.subcommands.career.AssassinSubCommand;
import xiamomc.survivalcompetition.Command.subcommands.career.WarriorSubCommand;
import xiamomc.survivalcompetition.Managers.IGameManager;

import java.util.List;

public class CareerCommand extends SCSubCommandHandler
{
    @Resolved
    private IGameManager game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (!game.DoesAllowCareerSelect())
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

    private final List<ISubCommand> subCommands = List.of(
            new AssassinSubCommand(),
            new WarriorSubCommand()
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
        return "设定自己的职业";
    }
}
