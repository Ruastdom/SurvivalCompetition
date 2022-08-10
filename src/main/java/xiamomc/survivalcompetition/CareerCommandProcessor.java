package xiamomc.survivalcompetition;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xiamomc.survivalcompetition.Managers.ICareerManager;

public class CareerCommandProcessor extends PluginObject implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        ICareerManager icm = (ICareerManager) Dependencies.Get(ICareerManager.class);
        if ( icm.addToCareer(sender.getName(), args[0]) ){
            sender.sendMessage("您已成功选择该职业");
            return true;
        } else {
            sender.sendMessage("选择该职业时出现错误，您是否已经有了一个职业？");
            return false;
        }
    }
}
