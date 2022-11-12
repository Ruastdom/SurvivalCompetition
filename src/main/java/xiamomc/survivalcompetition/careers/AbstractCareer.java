package xiamomc.survivalcompetition.careers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.SurvivalCompetition;

public abstract class AbstractCareer
{
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
        return display.toComponent().style(b ->
                b.hoverEvent(HoverEvent.showText(description.toComponent()))
                        .clickEvent(ClickEvent.runCommand("/setcareer" + " " + getIdentifier()))
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

    private static final FormattableMessage fallbackDisplay = new FormattableMessage(SurvivalCompetition.getInstance(), "Dummy");

    /**
     * 职业的显示名
     */
    protected FormattableMessage display = fallbackDisplay;

    /**
     * 获取职业的ID
     *
     * @return 职业的ID
     */
    public abstract NamespacedKey getIdentifier();

    public FormattableMessage getDescription()
    {
        return description;
    }

    /**
     * 职业描述
     */
    protected FormattableMessage description = fallbackDisplay;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof AbstractCareer career)) return false;

        return career.getIdentifier().equals(this.getIdentifier());
    }
}
