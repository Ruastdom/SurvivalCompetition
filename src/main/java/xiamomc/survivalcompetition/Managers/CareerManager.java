package xiamomc.survivalcompetition.Managers;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import xiamomc.survivalcompetition.Careers.AbstractCareer;
import xiamomc.survivalcompetition.Careers.AssassinCareer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CareerManager implements ICareerManager{
    private final List<AbstractCareer> careerList;

    private static final ConcurrentHashMap<Player, AbstractCareer> playerCareers = new ConcurrentHashMap<>();

    public CareerManager()
    {
        careerList = List.of(new AbstractCareer[]
                {
                        new AssassinCareer()
                });

        Bukkit.getServer().broadcast(Component.text(AssassinCareer.GetInternalNameStatic()));
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
