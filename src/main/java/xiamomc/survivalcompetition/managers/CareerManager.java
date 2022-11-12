package xiamomc.survivalcompetition.managers;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.survivalcompetition.SCPluginObject;
import xiamomc.survivalcompetition.careers.AbstractCareer;
import xiamomc.survivalcompetition.careers.AssassinCareer;
import xiamomc.survivalcompetition.careers.WarriorCareer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CareerManager extends SCPluginObject implements ICareerManager
{
    private final List<AbstractCareer> careerList;

    private static final ConcurrentHashMap<UUID, AbstractCareer> playerCareers = new ConcurrentHashMap<>();

    @Resolved
    private IGameManager game;

    public CareerManager()
    {
        careerList = List.of(
                new AssassinCareer(),
                new WarriorCareer()
        );
    }

    @Override
    public List<AbstractCareer> getCareerList()
    {
        return careerList;
    }

    @Override
    public void clear()
    {
        for (var entry : playerCareers.entrySet())
        {
            var uuid = entry.getKey();
            var player = Bukkit.getPlayer(entry.getKey());
            if (player == null) continue;

            var career = entry.getValue();
            career.resetFor(player);

            playerCareers.remove(uuid);
        }
    }

    @Override
    public boolean resetCareerFor(Player player)
    {
        var career = playerCareers.get(player.getUniqueId());

        if (career != null)
        {
            playerCareers.remove(player.getUniqueId());
            career.resetFor(player);

            return true;
        }

        return false;
    }

    @Override
    public boolean applyCareerFor(Player player, NamespacedKey identifier)
    {
        if (!game.allowCareerSelect()) return false;

        if (player == null || identifier == null) return false;

        var career = careerList.stream()
                .filter(c -> identifier.equals(c.getIdentifier()))
                .findFirst().orElse(null);

        if (career == null) return false;

        var playerUUID = player.getUniqueId();

        var currentPlayerCareer = playerCareers.get(playerUUID);

        //不要重复添加玩家到某一职业，并且在切换职业前先移除现有职业
        if (career.equals(currentPlayerCareer))
        {
            return false;
        }
        else if (currentPlayerCareer != null)
        {
            currentPlayerCareer.resetFor(player);
            playerCareers.remove(playerUUID);
        }

        //career.applyToPlayer()可能会抛出异常，所以先把玩家添加到playerCareers
        playerCareers.put(playerUUID, career);

        career.applyToPlayer(player);

        return true;
    }

    @Override
    public AbstractCareer getPlayerCareer(Player player)
    {
        return playerCareers.get(player.getUniqueId());
    }
}
