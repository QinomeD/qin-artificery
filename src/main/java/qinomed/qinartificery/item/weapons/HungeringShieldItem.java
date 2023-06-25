package qinomed.qinartificery.item.weapons;

import com.github.elenterius.biomancy.chat.ComponentUtil;
import com.github.elenterius.biomancy.client.util.ClientTextUtil;
import com.github.elenterius.biomancy.init.ModParticleTypes;
import com.github.elenterius.biomancy.init.ModSoundEvents;
import com.github.elenterius.biomancy.item.ICustomTooltip;
import com.github.elenterius.biomancy.item.IKeyListener;
import com.github.elenterius.biomancy.item.ItemCharge;
import com.github.elenterius.biomancy.item.livingtool.LivingTool;
import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.styles.TextComponentUtil;
import com.github.elenterius.biomancy.util.SoundUtil;
import com.github.elenterius.biomancy.util.fuel.NutrientFuelUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;
import qinomed.qinartificery.client.geckolib.HungeringShieldRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Consumer;

@ParametersAreNonnullByDefault
public class HungeringShieldItem extends ShieldItem implements LivingTool, ItemCharge, IAnimatable, IKeyListener, ICustomTooltip {
    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

    public HungeringShieldItem(Properties pProperties) {
        super(pProperties);
    }

    public static void playBloodyClawsFX(LivingEntity attacker) {
        attacker.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 0.85f, 0.9f + attacker.getRandom().nextFloat() * 0.5f);
        if (attacker.level instanceof ServerLevel serverLevel) {
            double xOffset = -Mth.sin(attacker.getYRot() * Mth.DEG_TO_RAD);
            double zOffset = Mth.cos(attacker.getYRot() * Mth.DEG_TO_RAD);
            serverLevel.sendParticles(ModParticleTypes.BLOODY_CLAWS_ATTACK.get(), attacker.getX() + xOffset, attacker.getY(0.52f), attacker.getZ() + zOffset, 0, xOffset, 0, zOffset, 0);
        }
    }

    public static void playBloodExplosionFX(LivingEntity target) {
        if (target.level instanceof ServerLevel serverLevel) {
            float w = target.getBbWidth() * 0.45f;
            double x = serverLevel.getRandom().nextGaussian() * w;
            double y = serverLevel.getRandom().nextGaussian() * 0.2d;
            double z = serverLevel.getRandom().nextGaussian() * w;
            serverLevel.sendParticles(ModParticleTypes.FALLING_BLOOD.get(), target.getX(), target.getY(0.5f), target.getZ(), 20, x, y, z, 0.25);
        }
    }

    @Override
    public int getMaxCharge(ItemStack itemStack) {
        return 50;
    }

    @Override
    public void onChargeChanged(ItemStack livingTool, int oldValue, int newValue) {
        if (newValue <= 0 && getLivingToolState(livingTool) == LivingToolState.AWAKENED) {
            setLivingToolState(livingTool, LivingToolState.DORMANT);
        }
    }

    @Override
    public int getLivingToolActionCost(ItemStack livingTool, LivingToolState state, ToolAction toolAction) {
        return toolAction == ToolActions.SHIELD_BLOCK ? switch (state) {
            case AWAKENED, DORMANT -> 1;
            case BROKEN -> 0;
        } : 0;
    }

    @Override
    public int getMaxNutrients(ItemStack itemStack) {
        return 250;
    }

    @Override
    public boolean hasNutrients(ItemStack container) {
        return getNutrients(container) > getLivingToolActionCost(container, LivingToolState.AWAKENED, ToolActions.SHIELD_BLOCK);
    }

    @Override
    public void onNutrientsChanged(ItemStack livingTool, int oldValue, int newValue) {
        LivingToolState prevState = getLivingToolState(livingTool);
        LivingToolState state = prevState;

        if (newValue <= 0) {
            if (state != LivingToolState.BROKEN) setLivingToolState(livingTool, LivingToolState.BROKEN);
            return;
        }

        if (state == LivingToolState.BROKEN) {
            state = LivingToolState.DORMANT;
        }

        int maxCost = getLivingToolMaxActionCost(livingTool, state);
        if (newValue < maxCost && state == LivingToolState.DORMANT) state = LivingToolState.BROKEN;

        if (state != prevState) setLivingToolState(livingTool, state);
    }

    @Override
    public void updateLivingToolState(ItemStack livingTool, ServerLevel level, Player player) {
        GeckoLibUtil.writeIDToStack(livingTool, level);

        LivingToolState state = getLivingToolState(livingTool);
        boolean hasNutrients = hasNutrients(livingTool);

        if (!hasNutrients) {
            if (state != LivingToolState.BROKEN) {
                setLivingToolState(livingTool, LivingToolState.BROKEN);
                SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_BREAK.get());
            }
            return;
        }

        switch (state) {
            case BROKEN, AWAKENED -> {
                setLivingToolState(livingTool, LivingToolState.DORMANT);
                SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_HIT.get());
            }
            case DORMANT -> {
                if (hasCharge(livingTool)) {
                    setLivingToolState(livingTool, LivingToolState.AWAKENED);
                    SoundUtil.broadcastItemSound(level, player, ModSoundEvents.FLESH_BLOCK_PLACE.get());
                }
            }
        }
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if (stack.getItem() instanceof HungeringShieldItem shieldItem && entity.isUsingItem() && entity.getUseItem() == stack) {
            entity.setDeltaMovement(entity.getViewVector(1).multiply(1.1, 1.1, 1.1));
        }

        return super.onEntitySwing(stack, entity);
    }

    @Override
    public int getNutrientFuelValue(ItemStack container, ItemStack food) {
        return NutrientFuelUtil.getFuelValue(food);
    }

    @Override
    public boolean handleOverrideOtherStackedOnMe(ItemStack livingTool, ItemStack potentialFood, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (LivingTool.super.handleOverrideOtherStackedOnMe(livingTool, potentialFood, slot, action, player, access)) {
            if (player.level instanceof ServerLevel serverLevel) {
                GeckoLibUtil.writeIDToStack(livingTool, serverLevel);
                SoundUtil.broadcastItemSound(serverLevel, player, SoundEvents.GENERIC_EAT);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean handleOverrideStackedOnOther(ItemStack livingTool, Slot slot, ClickAction action, Player player) {
        if (LivingTool.super.handleOverrideStackedOnOther(livingTool, slot, action, player)) {
            if (player.level instanceof ServerLevel serverLevel) {
                GeckoLibUtil.writeIDToStack(livingTool, serverLevel);
                SoundUtil.broadcastItemSound(serverLevel, player, SoundEvents.GENERIC_EAT);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultHolder<Byte> onClientKeyPress(ItemStack stack, Level level, Player player, EquipmentSlot slot, byte flags) {
        if (!hasCharge(stack)) {
            player.displayClientMessage(TextComponentUtil.getFailureMsgText("not_enough_charge"), true);
            player.playSound(SoundEvents.VILLAGER_NO, 0.8f, 0.8f + player.getLevel().getRandom().nextFloat() * 0.4f);
            return InteractionResultHolder.fail(flags);
        }

        return InteractionResultHolder.success(flags);
    }

    @Override
    public void onServerReceiveKeyPress(ItemStack stack, ServerLevel level, Player player, byte flags) {
        this.updateLivingToolState(stack, level, player);
    }

    private PlayState onAnim(AnimationEvent<HungeringShieldItem> event) {
        List<ItemStack> extraData = event.getExtraDataOfType(ItemStack.class);
        LivingToolState state = !extraData.isEmpty() ? getLivingToolState(extraData.get(0)) : LivingToolState.BROKEN;

        AnimationController<HungeringShieldItem> controller = event.getController();
        switch (state) {
            case DORMANT -> Animations.setDormant(controller);
            case AWAKENED -> Animations.setAwakened(controller);
            case BROKEN -> Animations.setBroken(controller);
        }

        return PlayState.CONTINUE;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        super.initializeClient(consumer);
        consumer.accept(new IClientItemExtensions() {
            private final HungeringShieldRenderer renderer = new HungeringShieldRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller", 1, this::onAnim));
    }

    @Override
    public AnimationFactory getFactory() {
        return animationFactory;
    }

    protected static class Animations {
        protected static final AnimationBuilder DORMANT = new AnimationBuilder().loop("animation.hungering_shield.dormant");
        protected static final AnimationBuilder TO_SLEEP_TRANSITION = new AnimationBuilder().playOnce("animation.hungering_shield.tosleep").loop("animation.hungering_shield.dormant");
        protected static final AnimationBuilder BROKEN = new AnimationBuilder().loop("animation.hungering_shield.broken");
        protected static final AnimationBuilder WAKEUP_TRANSITION = new AnimationBuilder().playOnce("animation.hungering_shield.wakeup").loop("animation.hungering_shield.awakened");
        protected static final AnimationBuilder AWAKENED = new AnimationBuilder().loop("animation.hungering_shield.awakened");

        private Animations() {}

        protected static void setDormant(AnimationController<?> controller) {
            Animation animation = controller.getCurrentAnimation();
            if (animation == null) {
                controller.setAnimation(DORMANT);
                return;
            }

            if (!animation.animationName.equals("hungering_shield.dormant")) {
                controller.setAnimation(TO_SLEEP_TRANSITION);
                return;
            }

            controller.setAnimation(DORMANT);
        }

        protected static void setAwakened(AnimationController<?> controller) {
            Animation animation = controller.getCurrentAnimation();
            if (animation == null) {
                controller.setAnimation(AWAKENED);
                return;
            }

            if (!animation.animationName.equals("hungering_shield.awakened")) {
                controller.setAnimation(WAKEUP_TRANSITION);
                return;
            }

            controller.setAnimation(AWAKENED);
        }

        public static void setBroken(AnimationController<HungeringShieldItem> controller) {
            controller.setAnimation(BROKEN);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.getItem() instanceof HungeringShieldItem shieldItem) {
            if (shieldItem.getLivingToolState(stack) != LivingToolState.BROKEN) {
                pPlayer.startUsingItem(pHand);
                return InteractionResultHolder.consume(stack);
            }
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return this.isValidEnchantment(stack, enchantment) && super.canApplyAtEnchantingTable(stack, enchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return this.getMaxStackSize(stack) == 1;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.getNutrients(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(this.getNutrientsPct(stack) * 13.0F);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 9742422;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(ComponentUtil.emptyLine());
        this.appendLivingToolTooltip(stack, tooltip);
        tooltip.add(ComponentUtil.emptyLine());
        tooltip.add(ClientTextUtil.pressButtonTo(ClientTextUtil.getDefaultKey(), TextComponentUtil.getTooltipText("action.cycle")));
    }

    @Override
    public Component getHighlightTip(ItemStack stack, Component displayName) {
        return ComponentUtil.mutable().append(displayName).append(" (").append(this.getLivingToolState(stack).getDisplayName()).append(")");
    }
}
