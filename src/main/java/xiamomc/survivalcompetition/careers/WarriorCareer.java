package xiamomc.survivalcompetition.careers;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import xiamomc.pluginbase.Messages.FormattableMessage;
import xiamomc.survivalcompetition.SurvivalCompetition;

public class WarriorCareer extends AbstractCareer
{
    public WarriorCareer()
    {
        var plugin = SurvivalCompetition.getInstance();

        display = new FormattableMessage(plugin, "战士");
        description = new FormattableMessage(plugin, "获得防护加成");
    }

    @Override
    public NamespacedKey getIdentifier()
    {
        return new NamespacedKey(SurvivalCompetition.getSCNameSpace(), "warrior");
    }

    private final AttributeModifier armorModifier = new AttributeModifier("xiamoModifier", 3, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public boolean applyToPlayer(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_ARMOR)
                .addModifier(armorModifier);

        return super.applyToPlayer(player);
    }

    @Override
    public boolean resetFor(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_ARMOR)
                .removeModifier(armorModifier);

        return super.resetFor(player);
    }
}
