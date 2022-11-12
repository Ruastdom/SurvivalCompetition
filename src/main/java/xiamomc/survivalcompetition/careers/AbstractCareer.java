package xiamomc.survivalcompetition.careers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
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
                b.decorate(TextDecoration.UNDERLINED)
                        .hoverEvent(HoverEvent.showText(description.toComponent()))
                        .clickEvent(ClickEvent.runCommand("/setcareer" + " " + getIdentifier()))
        );
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
