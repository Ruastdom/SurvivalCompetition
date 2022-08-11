package xiamomc.survivalcompetition.Careers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

public class AssassinCareer extends AbstractCareer
{
    public AssassinCareer()
    {
        DisplayName = "刺客";
        InternalName = "assassin";
        Description = "使用剑类物品攻击伤害增高 - 移动速度少许加快";

        Initialize();
    }

    private final AttributeModifier movementModifier = new AttributeModifier("xiamoModifier", 0.1, AttributeModifier.Operation.ADD_NUMBER);

    private final AttributeModifier attackDamageModifier = new AttributeModifier("xiamoModifier", 0.3, AttributeModifier.Operation.ADD_NUMBER);

    @Override
    public Boolean ApplyToPlayer(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .addModifier(movementModifier);

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .addModifier(attackDamageModifier);

        return super.ApplyToPlayer(player);
    }

    @Override
    public Boolean ResetFor(Player player)
    {
        if (player == null) return false;

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
                .removeModifier(movementModifier);

        player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .removeModifier(attackDamageModifier);

        return super.ResetFor(player);
    }
}
