package xiamomc.survivalcompetition.careers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class WarriorCareer extends AbstractCareer
{
    public WarriorCareer()
    {
        displayName = "战士";
        description = "获得防护加成";

        initialize();
    }

    @Override
    public String getInternalName()
    {
        return "warrior";
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
