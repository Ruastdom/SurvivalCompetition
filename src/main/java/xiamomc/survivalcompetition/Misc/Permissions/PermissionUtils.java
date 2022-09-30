package xiamomc.survivalcompetition.Misc.Permissions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionUtils
{
    public boolean HasPermission(CommandSender cs, PermissionNode pn)
    {
        var pnString = pn.toString();
        return cs.hasPermission(pnString);
    }

    public boolean HasPermission(Player player, PermissionNode pn)
    {
        var pnString = pn.toString();
        return player.hasPermission(pnString);
    }
}
