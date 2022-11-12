package xiamomc.survivalcompetition.managers.world;

import org.bukkit.PortalType;
import org.bukkit.World;

/**
 * Portal option
 *
 * @param env The environment
 * @param targetName Target world name
 */
public record PortalOption(String targetName, PortalType portalType)
{
    public static PortalOption option(String targetName, PortalType portalType)
    {
        return new PortalOption(targetName, portalType);
    }

    public static PortalOption[] options(PortalOption... options)
    {
        return options;
    }
}
