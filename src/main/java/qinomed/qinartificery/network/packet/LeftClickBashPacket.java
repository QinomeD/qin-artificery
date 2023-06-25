package qinomed.qinartificery.network.packet;

import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import qinomed.qinartificery.item.weapons.HungeringShieldItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LeftClickBashPacket {
    public LeftClickBashPacket() {

    }

    public LeftClickBashPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            ServerLevel level = player.getLevel();
            ItemStack stack = player.getUseItem();

            if (stack.getItem() instanceof HungeringShieldItem shieldItem && shieldItem.getLivingToolState(stack) == LivingToolState.AWAKENED) {
                player.setDeltaMovement(player.getViewVector(1).multiply(0.5, 0, 0.5));
                player.stopUsingItem();
                player.getCooldowns().addCooldown(shieldItem, 60);
                //update to client
                player.hurtMarked = true;

                SoundUtil.playItemSoundEffect(level, player, ModSoundEvents.FLESH_BLOCK_HIT);
                if (!player.getAbilities().instabuild)
                    shieldItem.consumeCharge(stack, 1);

                ArrayList<Vec3> sight = lineOfSight(player, 1, 0.1);
                if (!getEntitiesOnLine(sight, level, entity -> entity == player).isEmpty())
                    SoundUtil.playItemSoundEffect(level, player, SoundEvents.SHIELD_BLOCK);

                for (LivingEntity target : getEntitiesOnLine(sight, level, entity -> entity == player)) {
                    HungeringShieldItem.playBloodExplosionFX(target);
                    if (CombatUtil.getBleedEffectLevel(target) != 0)
                        CombatUtil.hurtWithBleed(target, 4 * CombatUtil.getBleedEffectLevel(target));
                    else
                        CombatUtil.hurtWithBleed(target, 3);
                }
            }
        });

        return true;
    }

    private static List<LivingEntity> getEntitiesOnLine(ArrayList<Vec3> line, Level level, Predicate<LivingEntity> exclude) {
        List<LivingEntity> ret = new ArrayList<>();
        AABB aabb;

        for (Vec3 vec : line) {
            aabb = new AABB(vec, vec).inflate(1, 1, 1);
            ret.addAll(level.getEntitiesOfClass(LivingEntity.class, aabb));
        }
        ret.removeIf(exclude);

        return ret.stream().distinct().collect(Collectors.toList());
    }

    private static ArrayList<Vec3> lineOfSight(Entity entity, double range, double scaling) {
        ArrayList<Vec3> line = new ArrayList<>();
        Vec3 vec3;
        for (double i = 0; i <= range; i+=scaling) {
            vec3 = entity.getLookAngle().scale(i).add(entity.getX(), entity.getY(), entity.getZ()).add(0, entity.getEyeHeight(), 0);
            line.add(vec3);
        }
        return line;
    }
}
