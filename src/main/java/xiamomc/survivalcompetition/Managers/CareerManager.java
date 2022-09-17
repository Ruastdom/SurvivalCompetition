package xiamomc.survivalcompetition.Managers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xiamomc.survivalcompetition.Careers.AbstractCareer;
import xiamomc.survivalcompetition.Careers.AssassinCareer;
import xiamomc.survivalcompetition.Careers.WarriorCareer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CareerManager implements ICareerManager{
    private final List<AbstractCareer> careerList;

    private static final ConcurrentHashMap<Player, AbstractCareer> playerCareers = new ConcurrentHashMap<>();

    public CareerManager()
    {
        careerList = List.of(new AbstractCareer[]
                {
                        new AssassinCareer(),
                        new WarriorCareer()
                });
    }

    @Override
    public List<AbstractCareer> getCareerList() {
        return careerList;
    }

    @Override
    public void clear() {
        playerCareers.forEach((player, career) -> career.ResetFor(player));
        playerCareers.clear();
    }

    @Override
    public boolean addToCareer(String playerName, String internalName) {

        var player = Bukkit.getPlayer(playerName);

        if (player == null) return false;

        var optional = careerList.stream()
                                             .filter(c -> Objects.equals(c.GetInternalName(), internalName))
                                             .findFirst();

        if (optional.isEmpty()) return false;

        var career = optional.get();
        var currentPlayerCareer = playerCareers.get(player);

        //不要重复添加玩家到某一职业，并且在切换职业前先移除现有职业
        if (currentPlayerCareer == career) return false;
        else if (currentPlayerCareer != null)
        {
            currentPlayerCareer.ResetFor(player);
            playerCareers.remove(player);
        };

        //career.ApplyToPlayer()可能会抛出异常，所以先把玩家添加到playerCareers
        playerCareers.put(player, career);

        career.ApplyToPlayer(player);

        return true;
    }

    @Override
    public AbstractCareer getPlayerCareer(String playerName) {
        var player = Bukkit.getPlayer(playerName);

        if (player == null) throw new RuntimeException("未能找到与" + playerName + "对应的玩家");

        return playerCareers.get(player);
    }
}
