package qinomed.qinartificery.item.weapons;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TestEffectItem extends SwordItem {
    public TestEffectItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        double x = player.getX();
        double y = player.getY() + player.getEyeHeight();
        double z = player.getZ();

        Vec3 pos = player.getViewVector(1);
        Vec3 playerPos = new Vec3(x, y, z).add(pos.multiply(2, 2, 2));

        var start = pos.multiply(1.5, 1, 1.5).yRot(((float) Math.toRadians(-90)));
        var end = pos.multiply(1.5, 1, 1.5).yRot(((float) Math.toRadians(90)));

        if (level instanceof ServerLevel serverLevel)
            drawParticleLine(serverLevel, ParticleTypes.FLAME, new Vec3(start.x + playerPos.x, playerPos.y, start.z + playerPos.z), new Vec3(end.x + playerPos.x, playerPos.y, end.z + playerPos.z), 20, 0);

        return super.use(level, player, usedHand);
    }

    public static <T extends ParticleOptions> void drawParticleLine(ServerLevel level, T type, Vec3 start, Vec3 end, int particleCount, double angle) {
        double height = Math.tan(angle)*start.distanceTo(end);
        start = start.subtract(0, height/2, 0);
        end = end.add(0, height/2, 0);

        Vec3 direction = start.subtract(end);
        double d = start.distanceTo(end) / -particleCount;

        for (int i=0;i < particleCount; i += 1) {
            Vec3 pos = start.add(direction.normalize().multiply(i*d, i*d, i*d));
            level.sendParticles(type, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
    }
}
