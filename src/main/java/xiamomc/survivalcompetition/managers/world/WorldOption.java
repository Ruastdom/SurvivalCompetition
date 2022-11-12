package xiamomc.survivalcompetition.managers.world;

import org.bukkit.World;
import org.bukkit.WorldType;
import org.jetbrains.annotations.Nullable;

public record WorldOption(String name,
                          @Nullable String seed,
                          World.Environment env,
                          WorldType type,
                          boolean spawnStructures,
                          String generator,
                          PortalOption[] portalOptions)
{
    public boolean isValid()
    {
        return name != null && env != null && type != null;
    }

    @Override
    public String toString()
    {
        return "WorldOption("
                + "name:" + name
                + ", seed:" + seed
                + ", env:" + env
                + ", type:" + type
                + ", spawnStructures:" + spawnStructures
                + ", generator:" + generator
                + ")";
    }
}
