package qinomed.qinartificery.item.weapons;

import com.github.elenterius.biomancy.item.livingtool.LivingToolState;
import com.github.elenterius.biomancy.item.weapon.RavenousClawsItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.Lazy;
import qinomed.qinartificery.client.geckolib.FamishedBladeRenderer;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

import java.util.List;
import java.util.function.Consumer;

public class FamishedBladeItem extends RavenousClawsItem implements IAnimatable {
    private final Lazy<Multimap<Attribute, AttributeModifier>> brokenAttributes;
    private final Lazy<Multimap<Attribute, AttributeModifier>> dormantAttributes;
    private final Lazy<Multimap<Attribute, AttributeModifier>> awakenedAttributes;
    private final AnimationFactory animationFactory = GeckoLibUtil.createFactory(this);

    public FamishedBladeItem(Tier tier, float attackDamage, float attackSpeed, int maxNutrients, Properties properties) {
        super(tier, attackDamage, attackSpeed, maxNutrients, properties);
        float attackSpeedModifier = (float)((double)attackSpeed - Attributes.ATTACK_SPEED.getDefaultValue());
        brokenAttributes = Lazy.of(() -> createDefaultAttributeModifiers(0, 0, -0.5f).build());
        dormantAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage, attackSpeedModifier, -0.5f).build());
        awakenedAttributes = Lazy.of(() -> createDefaultAttributeModifiers(-1 + attackDamage + 2, attackSpeedModifier, 0.5f).build());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.MAINHAND) {
            return switch (getLivingToolState(stack)) {
                case BROKEN -> brokenAttributes.get();
                case DORMANT -> dormantAttributes.get();
                case AWAKENED -> awakenedAttributes.get();
            };
        }
        return ImmutableMultimap.of();
    }

    private PlayState onAnim(AnimationEvent<FamishedBladeItem> event) {
        List<ItemStack> extraData = event.getExtraDataOfType(ItemStack.class);
        LivingToolState state = !extraData.isEmpty() ? getLivingToolState(extraData.get(0)) : LivingToolState.BROKEN;

        AnimationController<FamishedBladeItem> controller = event.getController();
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
            private final FamishedBladeRenderer renderer = new FamishedBladeRenderer();

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
        protected static final AnimationBuilder DORMANT = new AnimationBuilder().loop("animation.famished_blade.dormant");
        protected static final AnimationBuilder TO_SLEEP_TRANSITION = new AnimationBuilder().playOnce("animation.famished_blade.tosleep").loop("animation.famished_blade.dormant");
        protected static final AnimationBuilder BROKEN = new AnimationBuilder().loop("animation.famished_blade.broken");
        protected static final AnimationBuilder WAKEUP_TRANSITION = new AnimationBuilder().playOnce("animation.famished_blade.wakeup").loop("animation.famished_blade.awakened");
        protected static final AnimationBuilder AWAKENED = new AnimationBuilder().loop("animation.famished_blade.awakened");

        private Animations() {
        }

        protected static void setDormant(AnimationController<?> controller) {
            Animation animation = controller.getCurrentAnimation();
            if (animation == null) {
                controller.setAnimation(DORMANT);
                return;
            }

            if (!animation.animationName.equals("famished_blade.dormant")) {
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

            if (!animation.animationName.equals("famished_blade.awakened")) {
                controller.setAnimation(WAKEUP_TRANSITION);
                return;
            }

            controller.setAnimation(AWAKENED);
        }

        public static void setBroken(AnimationController<FamishedBladeItem> controller) {
            controller.setAnimation(BROKEN);
        }
    }
}
