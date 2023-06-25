package qinomed.qinartificery.item.weapons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.moonlight.api.item.IFirstPersonAnimationProvider;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonAnimationProvider;
import net.mehvahdjukaar.moonlight.api.misc.DualWeildState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import qinomed.qinartificery.entity.projectile.NephriteBombProjectile;
import qinomed.qinartificery.registry.EntityTypesRegistry;
import qinomed.qinartificery.registry.ItemRegistry;
import top.theillusivec4.curios.api.CuriosApi;

public class NephriteBombItem extends Item implements IThirdPersonAnimationProvider, IFirstPersonAnimationProvider {
    public NephriteBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        int xpCost = CuriosApi.getCuriosHelper().findCurios(pPlayer, ItemRegistry.NEPHRITE_PENDANT.get()).isEmpty() ? 20 : 10;
        if (pPlayer.totalExperience >= xpCost || pPlayer.getAbilities().instabuild)
            pPlayer.startUsingItem(pUsedHand);
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (entity instanceof Player player && !level.isClientSide() && timeCharged < 71984) {
            int xpCost = CuriosApi.getCuriosHelper().findCurios(entity, ItemRegistry.NEPHRITE_PENDANT.get()).isEmpty() ? 20 : 10;

            player.giveExperiencePoints(-xpCost);
            NephriteBombProjectile projectile = new NephriteBombProjectile(EntityTypesRegistry.NEPHRITE_BOMB_PROJECTILE.get(), level);
            projectile.setOwner(entity);
            projectile.setPos(new Vec3(player.getX(), player.getY(), player.getZ()).add(0, player.getEyeHeight(), 0));
            projectile.setDeltaMovement(player.getViewVector(1).multiply(0.8d, 0.8d, 0.8d));
            level.addFreshEntity(projectile);
            player.awardStat(Stats.ITEM_USED.get(this));
        }
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    private <T extends LivingEntity> void yeetPose(HumanoidModel<T> model, boolean leftHand) {
        ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;

        mainHand.xRot = (float) Math.toRadians(-180.0d);
    }

    @Override
    public <T extends LivingEntity> boolean poseRightArm(ItemStack itemStack, HumanoidModel<T> humanoidModel, T entity, HumanoidArm humanoidArm, DualWeildState dualWeildState) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.yeetPose(humanoidModel, false);
            return true;
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> boolean poseLeftArm(ItemStack itemStack, HumanoidModel<T> humanoidModel, T entity, HumanoidArm humanoidArm, DualWeildState dualWeildState) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.yeetPose(humanoidModel, true);
            return true;
        }
        return false;
    }

    @Override
    public boolean isTwoHanded() {
        return false;
    }

    @Override
    public void animateItemFirstPerson(LivingEntity entity, ItemStack stack, InteractionHand hand, PoseStack poseStack, float partialTicks, float pitch, float attackAnim, float handHeight) {
        if (entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0 && entity.getUsedItemHand() == hand) {
            int isOffhand = entity.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;

            float timeLeft = stack.getUseDuration() - (entity.getUseItemRemainingTicks() - partialTicks + 1.0F);
            float sin = Mth.sin((timeLeft - 0.1f) * 1.3f);

            poseStack.translate(0, isOffhand * (sin * 0.0038f + 0.2f), Math.min(Math.log(timeLeft) * 0.05f, 0.16f));
            poseStack.mulPose(Vector3f.XN.rotationDegrees(45));

        }
    }


}
