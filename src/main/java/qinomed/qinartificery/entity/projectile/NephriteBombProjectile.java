package qinomed.qinartificery.entity.projectile;

import net.mehvahdjukaar.moonlight.api.entity.ImprovedProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import qinomed.qinartificery.registry.ItemRegistry;
import qinomed.qinartificery.registry.SoundRegistry;

import java.util.List;

public class NephriteBombProjectile extends ImprovedProjectileEntity {

    public NephriteBombProjectile(EntityType<? extends ThrowableItemProjectile> type, Level world) {
        super(type, world);
        this.maxAge = 10;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        /*
        if (LDLib.isClient()) {
            spawnTrail();
        }

         */
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        Entity owner = this.getOwner();
        if (pResult.getType() != HitResult.Type.MISS && owner != null) {
            this.reachedEndOfLife();
        }
    }

    @Override
    public void reachedEndOfLife() {
        super.reachedEndOfLife();
        shootRays(this.getLevel(), this.getOwner());
        if (!level.isClientSide()) {
            ((ServerLevel) level).sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        }

        if (this.getOwner() != null)
            level.playSound(null, this.getOwner().getOnPos(), SoundRegistry.ZAP_EXPLOSION.get(), SoundSource.PLAYERS, 4f, 1f);
    }

    private void shootRays(Level level, Entity owner) {
        if (this.getOwner() != null) {
            Vec3 bombPos = new Vec3(this.getX(), this.getY(), this.getZ());

            AABB range = new AABB(new BlockPos(bombPos)).inflate(5d);

            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, range);
            targets.remove(owner);

            for (LivingEntity target : targets) {
                var targetPos = new Vec3(target.getX(), target.getY() + target.getEyeHeight(), target.getZ());
                /*
                if (LDLib.isClient()) {
                    spawnRay(targetPos);
                }

                 */

                //ParticleUtils.drawParticleLine((ServerLevel) level, ParticleRegistry.NEPHRITE_LASER_PARTICLES.get(), bombPos, targetPos, (int) bombPos.distanceTo(targetPos) * 10, 0);
                target.hurt(DamageSource.MAGIC, 7f);
            }
        }
    }

    /*
    @OnlyIn(Dist.CLIENT)
    private void spawnTrail() {
        FX trail = FXHelper.getFX(new ResourceLocation(QinArtificery.MODID, "nephrite_trail"));
        var trailEmitter = trail.emitters().get(0).copy();
        trailEmitter.setFXEffect(new EntityEffect(trail, level, this));
        trailEmitter.emmitToLevel(level, this.getX(), this.getY(), this.getZ());
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnRay(Vec3 targetPos) {
        FX trail = FXHelper.getFX(new ResourceLocation(QinArtificery.MODID, "nephrite_trail"));

        var trailEmitter = ((TrailEmitter) trail.emitters().get(0).copy());
        trailEmitter.setFXEffect(new EntityEffect(trail, level, this));
        trailEmitter.getTails().addLast(new Vector3(targetPos));
        trailEmitter.emmitToLevel(level, this.getX(), this.getY(), this.getZ());
    }

     */

    @Override
    protected Item getDefaultItem() {
        return ItemRegistry.NEPHRITE_BOMB.get();
    }
}
