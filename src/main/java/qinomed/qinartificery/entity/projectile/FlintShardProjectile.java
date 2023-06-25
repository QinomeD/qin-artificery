package qinomed.qinartificery.entity.projectile;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import qinomed.qinartificery.item.weapons.magic.BaseRuneItem;

public class FlintShardProjectile extends Projectile {
    private int life;
    private Item rune;

    public FlintShardProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.life = 100;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();

        this.life--;
        if (life <= 0)
            this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);

        if (rune instanceof BaseRuneItem runeItem && this.getOwner() instanceof Player player && pResult.getEntity() instanceof LivingEntity target) {
            runeItem.enemyHit(player, target);
        }
    }

    public Item getRune() {
        return rune;
    }

    public void setRune(Item rune) {
        this.rune = rune;
    }
}
