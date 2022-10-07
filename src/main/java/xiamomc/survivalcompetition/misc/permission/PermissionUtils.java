package xiamomc.survivalcompetition.misc.permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionUtils
{
    public boolean hasPermission(CommandSender cs, PermissionNode pn)
    {
        var pnString = pn.toString();
        return cs.hasPermission(pnString);
    }

    public boolean hasPermission(Player player, PermissionNode pn)
    {
        var pnString = pn.toString();
        return player.hasPermission(pnString);
    }
}
