package xiamomc.survivalcompetition.Command.subcommands.career;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.survivalcompetition.Managers.ICareerManager;
import xiamomc.survivalcompetition.SCPluginObject;

public class WarriorSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "warrior";
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public String getHelpMessage()
    {
        return "战士职业";
    }

    @Resolved
    private ICareerManager icm;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull String[] strings)
    {
        if (icm.addToCareer(commandSender.getName(), getCommandName()))
        {
            commandSender.sendMessage("您已成功选择该职业");
            return true;
        }
        else
        {
            commandSender.sendMessage("选择该职业时出现错误，您是否已经有了一个职业？");
            return true; //暂时返回true来避免显示"/setcareer career"
        }
    }
}
