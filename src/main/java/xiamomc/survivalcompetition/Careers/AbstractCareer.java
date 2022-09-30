package xiamomc.survivalcompetition.Careers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class AbstractCareer
{
    /**
     * 应用职业到玩家
     *
     * @param playerName 目标玩家名
     * @return 是否成功
     */
    public boolean applyToPlayer(String playerName)
    {
        return applyToPlayer(Bukkit.getPlayer(playerName));
    }

    /**
     * 应用职业到玩家
     *
     * @param player 目标玩家
     * @return 是否成功
     */
    public boolean applyToPlayer(Player player)
    {
        return player != null;
    }

    /**
     * 移除某一玩家的职业效果
     *
     * @param playerName 目标玩家名
     * @return 是否成功
     */
    public boolean resetFor(String playerName)
    {
        return resetFor(Bukkit.getPlayer(playerName));
    }

    /**
     * 移除某一玩家的职业效果
     *
     * @param player 目标玩家
     * @return 是否成功
     */
    public boolean resetFor(Player player)
    {
        return player != null;
    }

    /**
     * 获取这一职业的显示文本
     *
     * @return 用来显示的文本
     */
    public Component getNameAsComponent()
    {
        return careerNameAsComponent;
    }

    /**
     * 初始化职业
     */
    protected void initialize()
    {
        careerNameAsComponent = Component.text(displayName).style(b ->
                b.decorate(TextDecoration.BOLD)
                        .decorate(TextDecoration.UNDERLINED)
                        .color(TextColor.color(16755200))
                        .hoverEvent(HoverEvent.showText(Component.text(description)))
                        .clickEvent(ClickEvent.runCommand("/setcareer" + " " + getInternalName()))
        );
    }

    /**
     * 处理事件
     *
     * @param player 目标玩家
     * @param e      事件
     * @return 处理是否成功
     * @apiNote 一般来说职业实现不会检查玩家是否拥有此职业，因为在职业管理器中已经有一个玩家 -> 职业的ConcurrentHashMap了
     */
    public boolean onEvent(Player player, Event e)
    {
        return true;
    }

    /**
     * 职业的显示名
     */
    protected String displayName = "Dummy";

    private Component careerNameAsComponent;

    /**
     * 获取职业的内部名
     *
     * @return 职业的内部名
     */
    public abstract String getInternalName();

    /**
     * 职业描述
     */
    protected String description = "虚拟职业";
}
