package xiamomc.survivalcompetition.Managers;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CareerManager implements ICareerManager{
    private static List<BaseComponent> careerList = new ArrayList<>();
    private static Map playerCareers = new HashMap();
    @Override
    public List<BaseComponent> getCareerList() {
        return careerList;
    }

    public void initCareersComponents(){
        // 刺客 - 使用剑类物品攻击伤害增高 - 移动速度少许加快
        BaseComponent assassin = new TextComponent("刺客");
        assassin.setColor(ChatColor.GOLD); // 颜色
        assassin.setBold(true); // 加粗
        assassin.setUnderlined(true); // 下划线
        assassin.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("使用剑类物品攻击伤害增高 - 移动速度少许加快")));
        assassin.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/setcareer assassin"));
        careerList.add(assassin);
    }

    @Override
    public void clear() {
        careerList.clear();
        playerCareers.clear();
    }

    @Override
    public boolean addToCareer(String playerName, String careerName) {
        if (playerCareers.containsKey(playerName)) {
            return false;
        } else {
            playerCareers.put(playerName, careerName);
            switch (careerName){
                case "assassin":
                    setAssassinEffects(playerName);
            }
            return true;
        }
    }

    @Override
    public String getPlayerCareer(String playerName) {
        return (String) playerCareers.get(playerName);
    }

    private void setAssassinEffects(String playerName) {
        PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 4500, 0, false, false, false);
        speed.apply(Bukkit.getPlayer(playerName));
        PotionEffect strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 4500, 0, false, false, false);
        strength.apply(Bukkit.getPlayer(playerName));
    }
}
