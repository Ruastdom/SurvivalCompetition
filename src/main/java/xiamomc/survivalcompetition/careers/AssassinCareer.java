package xiamomc.survivalcompetition.careers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class AssassinCareer extends AbstractCareer
{
    public AssassinCareer()
    {
        displayName = "刺客";
        description = "使用剑类物品攻击伤害增高 - 移动速度少许加快";

        initialize();
    }

    @Override
    public String getInternalName()
    {
        return "assassin";
    }

    private final AttributeModifier movementModifier = new AttributeModifier("xiamoModifier", 0.1, AttributeModifier.Operation.ADD_SCALAR);

    private final AttributeModifier attackDamageModifier = new AttributeModifier("xiamoModifier", 0.3, AttributeModifier.Operation.ADD_SCALAR);

    @Override
    public boolean applyToPlayer(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .addModifier(movementModifier);

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .addModifier(attackDamageModifier);

        return super.applyToPlayer(player);
    }

    @Override
    public boolean resetFor(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .removeModifier(movementModifier);

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .removeModifier(attackDamageModifier);

        return super.resetFor(player);
    }
}
