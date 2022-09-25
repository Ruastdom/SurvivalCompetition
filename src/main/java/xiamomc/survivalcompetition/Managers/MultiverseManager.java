package xiamomc.survivalcompetition.Managers;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.multiverseinventories.MultiverseInventories;
import com.onarandombox.multiverseinventories.WorldGroup;
import com.onarandombox.multiverseinventories.profile.WorldGroupManager;
import com.onarandombox.multiverseinventories.share.Sharables;
import org.bukkit.*;
import org.bukkit.entity.Player;
import xiamomc.survivalcompetition.Annotations.Initializer;
import xiamomc.survivalcompetition.Misc.PluginObject;

public class MultiverseManager extends PluginObject implements IMultiverseManager
{
    MultiverseCore core;
    MultiverseNetherPortals netherPortals;
    MultiverseInventories inventories;
    WorldGroupManager groupManager ;
    MVWorldManager worldManager;

    @Initializer
    private void init()
    {
        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        netherPortals = (MultiverseNetherPortals) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-NetherPortals");
        inventories = (MultiverseInventories) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Inventories");

        groupManager = inventories.getGroupManager();
        worldManager = core.getMVWorldManager();
    }

    @Override
    public boolean createWorlds(String worldName)
    {
        boolean createOverworld = worldManager.addWorld(
                worldName, // The worldname
                World.Environment.NORMAL, // The overworld environment type.
                null, // The world seed. Any seed is fine for me, so we just pass null.
                WorldType.NORMAL, // Nothing special. If you want something like a flat world, change this.
                true, // This means we want to structures like villages to generator, Change to false if you don't want this.
                null // Specifies a custom generator. We are not using any so we just pass null.
        );
        boolean createNetherWorld = worldManager.addWorld(
                worldName + "_nether", // The worldname
                World.Environment.NETHER, // The overworld environment type.
                null, // The world seed. Any seed is fine for me, so we just pass null.
                WorldType.NORMAL, // Nothing special. If you want something like a flat world, change this.
                true, // This means we want to structures like villages to generator, Change to false if you don't want this.
                null // Specifies a custom generator. We are not using any so we just pass null.
        );
        boolean createEndWorld = worldManager.addWorld(
                worldName + "_end", // The worldname
                World.Environment.THE_END, // The overworld environment type.
                null, // The world seed. Any seed is fine for me, so we just pass null.
                WorldType.NORMAL, // Nothing special. If you want something like a flat world, change this.
                true, // This means we want to structures like villages to generator, Change to false if you don't want this.
                null // Specifies a custom generator. We are not using any so we just pass null.
        );

        var newOverworld = worldManager.getMVWorld(worldName);
        var newNether = worldManager.getMVWorld(worldName + "_nether");
        var newEnd = worldManager.getMVWorld(worldName + "_end");

        newOverworld.setGameMode(GameMode.SURVIVAL);

        newNether.setRespawnToWorld(worldName);
        newNether.setGameMode(GameMode.SURVIVAL);

        newEnd.setRespawnToWorld(worldName);
        newEnd.setGameMode(GameMode.SURVIVAL);

        boolean loadOverworld = worldManager.loadWorld(worldName);
        boolean loadNetherWorld = worldManager.loadWorld(worldName + "_nether");
        boolean loadEndWorld = worldManager.loadWorld(worldName + "_end");
        return createOverworld && createNetherWorld && createEndWorld && loadEndWorld && loadNetherWorld && loadOverworld;
    }

    @Override
    public boolean deleteWorlds(String worldName)
    {
        boolean deleteOverworld = worldManager.deleteWorld(worldName);
        boolean deleteNetherWorld = worldManager.deleteWorld(worldName + "_nether");
        boolean deleteEndWorld = worldManager.deleteWorld(worldName + "_end");
        return deleteOverworld && deleteNetherWorld && deleteEndWorld;
    }

    @Override
    public void createSMPWorldGroup(String worldName)
    {
        // Create new group named after the world.
        // Note this does not add group to Multiverse-Inventories knowledge yet.
        WorldGroup newGroup = groupManager.newEmptyGroup(worldName);

        // Add the 3 usual SMP world dims.
        newGroup.addWorld(worldName);
        newGroup.addWorld(worldName + "_nether");
        newGroup.addWorld(worldName + "_end");

        // Set to shares to all, so player data is consistent in overworld, nether and end.
        newGroup.getShares().addAll(Sharables.allOf());

        // Finally we add it to Multiverse-Inventories knowledge.
        // This step is important, else your WorldGroup will not work!
        groupManager.updateGroup(newGroup);
    }

    @Override
    public boolean linkSMPWorlds(String worldName)
    {
        boolean netherlinkf = netherPortals.addWorldLink(worldName, worldName + "_nether", PortalType.NETHER);
        boolean netherlinks = netherPortals.addWorldLink(worldName + "_nether", worldName, PortalType.NETHER);
        boolean endlinkf = netherPortals.addWorldLink(worldName, worldName + "_end", PortalType.ENDER);
        boolean endlinks = netherPortals.addWorldLink(worldName + "_end", worldName, PortalType.ENDER);
        return netherlinkf && netherlinks && endlinkf && endlinks;
    }

    @Override
    public void tpToWorld(Player player, String worldName)
    {
        core.teleportPlayer(player, player, Bukkit.getWorld(worldName).getSpawnLocation());
    }

    @Override
    public String GetFirstSpawnWorldName()
    {
        return core.getMVWorldManager().getFirstSpawnWorld().getName();
    }
}
