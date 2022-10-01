package xiamomc.survivalcompetition.Command;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.pluginbase.Command.IPluginCommand;
import xiamomc.survivalcompetition.Managers.ICareerManager;
import xiamomc.survivalcompetition.Managers.IGameManager;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;

public class CareerCommand extends SCPluginObject implements IPluginCommand
{
    @Resolved
    private ICareerManager icm;

    @Resolved
    private IGameManager game;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if (args.length < 1) return false;

        if (!game.DoesAllowCareerSelect())
        {
            sender.sendMessage(Component.translatable("现在还不能选择职业"));
            return true;
        }

        if (icm.addToCareer(sender.getName(), args[0]))
        {
            sender.sendMessage("您已成功选择该职业");
            return true;
        }
        else
        {
            sender.sendMessage("选择该职业时出现错误，您是否已经有了一个职业？");
            return true; //暂时返回true来避免显示"/setcareer career"
        }
    }

    @Override
    public String getCommandName()
    {
        return "setcareer";
    }

    @Override
    public List<String> onTabComplete(String baseName, String[] args, CommandSender source)
    {
        return null;
    }
}
