package xiamomc.survivalcompetition.managers;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import com.onarandombox.multiverseinventories.profile.WorldGroupManager;
import com.onarandombox.multiverseinventories.share.Sharables;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.bukkit.*;
import org.bukkit.entity.Player;
import xiamomc.pluginbase.Annotations.Initializer;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.survivalcompetition.SCPluginObject;
import xiamomc.survivalcompetition.managers.world.PortalOption;
import xiamomc.survivalcompetition.managers.world.WorldOption;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MultiverseManager extends SCPluginObject implements IMultiverseManager
{
    @Resolved
    private MultiverseCore core;

    @Resolved
    private MultiverseNetherPortals portals;

    @Resolved
    private MultiverseInventories inventories;

    private WorldGroupManager groupManager;
    private MVWorldManager worldManager;

    @Initializer
    private void init()
    {
        groupManager = inventories.getGroupManager();
        worldManager = core.getMVWorldManager();

        var ver = plugin.getDescription().getVersion().hashCode();
        inventoryGroupName = "SC_INV_REV_" + ver;
    }

    @Override
    public boolean createWorlds(String worldName)
    {
        currentWorlds.clear();
        currentWorldsAsBukkit.clear();

        var netherName = worldName + "_nether";
        var theEndName = worldName + "_end";

        var targetWorlds = new WorldOption[]
        {
            new WorldOption(worldName, null, World.Environment.NORMAL, WorldType.NORMAL, true, null,
                    PortalOption.options(PortalOption.option(netherName, PortalType.NETHER), PortalOption.option(theEndName, PortalType.ENDER))),

            new WorldOption(netherName, null, World.Environment.NETHER, WorldType.NORMAL, true, null,
                    PortalOption.options(PortalOption.option(worldName, PortalType.NETHER), PortalOption.option(theEndName, PortalType.ENDER))),

            new WorldOption(theEndName, null, World.Environment.THE_END, WorldType.NORMAL, true, null,
                    PortalOption.options(PortalOption.option(worldName, PortalType.ENDER), PortalOption.option(netherName, PortalType.NETHER))),
        };

        var result = generateWorlds(worldName, targetWorlds);

        if (result.success)
        {
            result.worlds.forEach(mvw ->
            {
                currentWorlds.add(mvw);
                currentWorldsAsBukkit.add(mvw.getCBWorld());
            });
        }

        updateInventoryGroup();

        return result.success;
    }

    private GenerateResult generateWorlds(String primaryWorldName, WorldOption[] options)
    {
        if (options == null || primaryWorldName == null) return new GenerateResult(false, null);

        var worlds = new ObjectArrayList<MultiverseWorld>();

        boolean success = true;

        for (var worldOption : options)
        {
            if (!worldOption.isValid())
                throw new IllegalArgumentException("Invalid option: " + worldOption);

            var worldName = worldOption.name();
            var worldEnvironment = worldOption.env();

            success = success && worldManager.addWorld(worldName, worldEnvironment, worldOption.seed(),
                    worldOption.type(), worldOption.spawnStructures(), worldOption.generator(),
                    true);

            // Is world added successfully?
            if (success)
            {
                var world = worldManager.getMVWorld(worldName);
                worlds.add(world);

                // Set respawn world and gamemode
                world.setRespawnToWorld(primaryWorldName);
                world.setGameMode(GameMode.SURVIVAL);

                // Link worlds by option
                for (var portalOption : worldOption.portalOptions())
                    portals.addWorldLink(worldName, portalOption.targetName(), portalOption.portalType());
            }
            else
                logger.error("Error occurred while creating world.");
        }

        // Are all operations executed successful?
        if (success)
            worlds.forEach(w -> worldManager.loadWorld(w.getName()));

        return new GenerateResult(success, worlds);
    }

    private record GenerateResult(boolean success, List<MultiverseWorld> worlds) { }

    private final List<MultiverseWorld> currentWorlds = new ObjectArrayList<>();
    private final List<World> currentWorldsAsBukkit = new ObjectArrayList<>();

    @Override
    public List<World> getCurrentWorlds() { return currentWorldsAsBukkit; }

    @Override
    public boolean deleteWorlds(String worldName)
    {
        AtomicBoolean success = new AtomicBoolean(true);
        currentWorlds.forEach(mvw -> success.set(success.get() && worldManager.deleteWorld(mvw.getName())));

        return success.get();
    }

    private static String inventoryGroupName = "SC_INV_REV_unknown";

    public void updateInventoryGroup()
    {
        // Get or create WorldGroup
        var group = groupManager.getGroup(inventoryGroupName);

        if (group == null)
            group = groupManager.newEmptyGroup(inventoryGroupName);

        // Remove all worlds previously set
        group.removeAllWorlds();

        // Add new worlds
        for (var mvw : currentWorlds)
            group.addWorld(mvw.getName());

        // Set to shares to all, so player data is consistent in overworld, nether and end.
        group.getShares().addAll(Sharables.allOf());

        // Finally we add it to Multiverse-Inventories knowledge.
        // This step is important, else your WorldGroup will not work!
        groupManager.updateGroup(group);
    }

    @Override
    public void tpToWorld(Player player, String worldName)
    {
        var world = Bukkit.getWorld(worldName);

        if (world != null)
            core.teleportPlayer(player, player, world.getSpawnLocation());
        else
            logger.error("Can't teleport player to non-existing world '" + worldName + "'");
    }

    @Override
    public String getFirstSpawnWorldName()
    {
        return core.getMVWorldManager().getFirstSpawnWorld().getName();
    }
}
