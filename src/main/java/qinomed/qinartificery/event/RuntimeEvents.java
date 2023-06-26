package qinomed.qinartificery.event;

import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.util.CombatUtil;
import com.mojang.blaze3d.platform.InputConstants;
import com.ordana.spelunkery.reg.ModBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.item.weapons.HungeringShieldItem;
import qinomed.qinartificery.network.ModMessages;
import qinomed.qinartificery.network.packet.LeftClickBashPacket;
import qinomed.qinartificery.registry.ItemRegistry;
import qinomed.qinartificery.util.CurioUtils;

@Mod.EventBusSubscriber(modid = QinArtificery.MODID)
public class RuntimeEvents {

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (event.getSource().getEntity() instanceof Player player) {
            ItemStack weapon = player.getItemInHand(player.getUsedItemHand());

            // Halite Baton
            if (target.isInvertedHealAndHarm() && weapon.getItem() == ItemRegistry.HALITE_BATON.get()) {
                event.setAmount(event.getAmount() + 3.5f);
                player.magicCrit(target);
            }

            // Wooden Cross
            if (CurioUtils.isEquipped(player, ItemRegistry.WOODEN_CROSS.get())) {
                if (target.isInvertedHealAndHarm()) {
                    event.setAmount(event.getAmount() + 1);
                }
            }

            // Lead Cross
            if (CurioUtils.isEquipped(player, ItemRegistry.LEAD_CROSS.get())) {
                if (target.isInvertedHealAndHarm()) {
                    event.setAmount(event.getAmount() + 2);
                }

                if (target.getArmorCoverPercentage() > 0) {
                    event.setAmount(event.getAmount() + 1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(ShieldBlockEvent event) {
        LivingEntity blocker = event.getEntity();
        DamageSource source = event.getDamageSource();
        ItemStack stack = blocker.getUseItem();
        LivingEntity attacker = (LivingEntity) event.getDamageSource().getEntity();

        if (stack.getItem() == ItemRegistry.CHAINED_CLAYMORE.get()) {
            if (source.isExplosion())
                event.setBlockedDamage(0);

            if (!source.isProjectile())
                event.setBlockedDamage(event.getBlockedDamage() * 0.75f);

            event.setShieldTakesDamage(true);
        }

        if (stack.getItem() instanceof HungeringShieldItem shieldItem) {
            if (shieldItem.getLivingToolState(stack) == LivingToolState.DORMANT)
                shieldItem.addCharge(blocker.getUseItem(), 5);

            if (blocker instanceof Player player && !player.getAbilities().instabuild)
                shieldItem.consumeNutrients(stack, (int) Math.ceil(event.getBlockedDamage()));

            switch (shieldItem.getLivingToolState(stack)) {
                case DORMANT:
                    if (attacker.getRandom().nextInt(10) == 0) { // 10%
                        HungeringShieldItem.playBloodyClawsFX(attacker);
                        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1f, 1.5f);

                        CombatUtil.applyBleedEffect(attacker, 20);
                    }

                case AWAKENED:
                    if (attacker.getRandom().nextInt(5) == 0) { //20%
                        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, attacker.getSoundSource(), 1f, 1.5f);

                        CombatUtil.applyBleedEffect(attacker, 20);
                    }
            }
        }
    }

    @SubscribeEvent
    public static void entityDeath(LivingDeathEvent event) {
        Entity attacker = event.getSource().getEntity();
        LivingEntity target = event.getEntity();
        Level level = target.getLevel();

        if (attacker instanceof Player player) {
            ItemStack weapon = player.getItemInHand(player.getUsedItemHand());
            if (weapon.getItem() == ItemRegistry.HALITE_BATON.get()) {
                var pos = target.getOnPos().above();
                if (level.getBlockState(pos).getMaterial().isReplaceable() && level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) {
                    level.setBlock(pos, ModBlocks.SALT.get().defaultBlockState(), 3);
                }
            }
        }
    }

    @SubscribeEvent
    public static void itemStackedOnOther(ItemStackedOnOtherEvent event) {
        Player player = event.getPlayer();
        ItemStack stackedOn = event.getStackedOnItem();
        ItemStack carriedStack = event.getCarriedItem();
        Slot slot = event.getSlot();

        if (stackedOn.getItem() instanceof HungeringShieldItem shieldItem) {
            if (shieldItem.handleOverrideOtherStackedOnMe(stackedOn, carriedStack, slot, event.getClickAction(), player, event.getCarriedSlotAccess())) {
                event.setCanceled(true);
            }
        }

        if (carriedStack.getItem() instanceof HungeringShieldItem shieldItem) {
            if (shieldItem.handleOverrideStackedOnOther(carriedStack, slot, event.getClickAction(), player)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void leftClick(InputEvent.MouseButton.Post event) {
        if (event.getButton() == InputConstants.MOUSE_BUTTON_LEFT && event.getAction() == InputConstants.PRESS && Minecraft.getInstance().level != null)
            ModMessages.sendToServer(new LeftClickBashPacket());
    }
}
