package xiamomc.survivalcompetition.Command.subcommands.career;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.ISubCommand;
import xiamomc.survivalcompetition.Managers.ICareerManager;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;

public class AssassinSubCommand extends SCPluginObject implements ISubCommand
{
    @Override
    public String getCommandName()
    {
        return "assassin";
    }

    @Override
    public List<String> onTabComplete(List<String> list, CommandSender commandSender)
    {
        return null;
    }

    @Override
    public String getPermissionRequirement()
    {
        return null;
    }

    @Override
    public String getHelpMessage()
    {
        return "刺客职业";
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
