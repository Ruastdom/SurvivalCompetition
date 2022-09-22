package xiamomc.survivalcompetition.Careers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class WarriorCareer extends AbstractCareer
{
    public WarriorCareer()
    {
        DisplayName = "战士";
        InternalName = "warrior";
        Description = "获得防护加成";

        Initialize();
    }

    private final AttributeModifier armorModifier = new AttributeModifier("xiamoModifier", 3, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public boolean ApplyToPlayer(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_ARMOR)
                .addModifier(armorModifier);

        return super.ApplyToPlayer(player);
    }

    @Override
    public boolean ResetFor(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_ARMOR)
                .removeModifier(armorModifier);

        return super.ResetFor(player);
    }
}
