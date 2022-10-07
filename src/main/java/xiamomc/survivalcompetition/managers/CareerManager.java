package xiamomc.survivalcompetition.managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xiamomc.pluginbase.Annotations.Resolved;
import xiamomc.survivalcompetition.careers.AbstractCareer;
import xiamomc.survivalcompetition.careers.AssassinCareer;
import xiamomc.survivalcompetition.careers.WarriorCareer;
import xiamomc.survivalcompetition.SCPluginObject;

import java.util.List;
import java.util.Objects;
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
        careerList = List.of(new AbstractCareer[]
                {
                        new AssassinCareer(),
                        new WarriorCareer()
                });
    }

    @Override
    public List<AbstractCareer> getCareerList()
    {
        return careerList;
    }

    @Override
    public void clear()
    {
        for (var uuid : playerCareers.keySet())
        {
            var player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            this.getPlayerCareer(player).resetFor(player);
            playerCareers.remove(uuid);
        }
    }

    @Override
    public boolean addToCareer(String playerName, String internalName)
    {
        if (!game.DoesAllowCareerSelect()) return false;

        var player = Bukkit.getPlayer(playerName);

        if (player == null) return false;

        var playerUUID = player.getUniqueId();

        var optional = careerList.stream()
                .filter(c -> Objects.equals(c.getInternalName(), internalName))
                .findFirst();

        if (optional.isEmpty()) return false;

        var career = optional.get();
        var currentPlayerCareer = playerCareers.get(playerUUID);

        //不要重复添加玩家到某一职业，并且在切换职业前先移除现有职业
        if (currentPlayerCareer == career)
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
