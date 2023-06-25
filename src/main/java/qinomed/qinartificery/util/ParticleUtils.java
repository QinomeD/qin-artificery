package qinomed.qinartificery.util;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public class ParticleUtils {

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
