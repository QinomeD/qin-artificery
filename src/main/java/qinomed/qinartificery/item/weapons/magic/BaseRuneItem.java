package qinomed.qinartificery.item.weapons.magic;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class BaseRuneItem extends Item {
    private final float damageModifier;

    public BaseRuneItem(Properties properties, float damageModifier) {
        super(properties);
        this.damageModifier = damageModifier;
    }

    private BaseRuneItem(Properties pProperties) {
        super(pProperties);
        this.damageModifier = 0;
    }

    public void enemyHit(Player caster, LivingEntity target) {
        target.hurt(DamageSource.MAGIC, damageModifier);
    }
}
