package xiamomc.survivalcompetition.commands.subcommands.career;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.SCPluginObject;
import xiamomc.survivalcompetition.careers.AbstractCareer;
import xiamomc.survivalcompetition.managers.ICareerManager;

public class GenericCareerSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return career.getIdentifier().asString();
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public FormattableMessage getHelpMessage()
    {
        return career.getDescription();
    }

    private final AbstractCareer career;

    public GenericCareerSubCommand(AbstractCareer ac)
    {
        this.career = ac;
    }

    @Resolved
    private ICareerManager icm;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull String[] strings)
    {
        if (!(commandSender instanceof Player))
        {
            commandSender.sendMessage("Can only apply career to player.");
            return true;
        }

        if (icm.applyCareerFor(commandSender.getName(), career.getIdentifier()))
            commandSender.sendMessage("您已成功选择该职业");
        else
            commandSender.sendMessage("选择该职业时出现错误，您是否已经有了一个职业？");

        return true;
    }
}
