package qinomed.qinartificery.item.weapons;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.mehvahdjukaar.moonlight.api.item.IFirstPersonAnimationProvider;
import net.mehvahdjukaar.moonlight.api.item.IThirdPersonAnimationProvider;
import net.mehvahdjukaar.moonlight.api.misc.DualWeildState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class ChainedClaymoreItem extends SwordItem implements IFirstPersonAnimationProvider, IThirdPersonAnimationProvider {
    private boolean shielding;
    private int slashAnimCounter;

    public ChainedClaymoreItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BLOCK;
    }

    public int getUseDuration(ItemStack pStack) {
        return 72000;
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
        if (!pIsSelected) {
            this.shielding = false;
            this.slashAnimCounter = 0;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        this.shielding = true;
        pPlayer.startUsingItem(pUsedHand);

        return super.use(pLevel, pPlayer, pUsedHand);
    }

    @Override
    public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeCharged) {
        super.releaseUsing(pStack, pLevel, pLivingEntity, pTimeCharged);

        this.shielding = false;
        this.slashAnimCounter = 100;

        if (pLivingEntity instanceof Player player && pTimeCharged < 71980) {
            player.setDeltaMovement(pLivingEntity.getViewVector(1).multiply(0.3, 0,0.3));
            //player.getCooldowns().addCooldown(pStack.getItem(), 100);

            AABB hitbox = player.getBoundingBox().inflate(1.5, 0.25, 1.5);
            hitbox = hitbox.move(player.getViewVector(1).multiply(2, 0, 2));

            var entities = pLevel.getEntitiesOfClass(LivingEntity.class, hitbox, entity -> entity != player);
            for (LivingEntity entity : entities) {
                entity.hurt(DamageSource.playerAttack(player), 7);
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
                if (pLevel instanceof ServerLevel serverLevel)
                    serverLevel.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY()+0.3, entity.getZ(), 20, 0.3, 0.2, 0.3, 0.1);
            }

            pLevel.playSound(null, player.getOnPos().above(), !entities.isEmpty() ? SoundEvents.ANVIL_LAND : SoundEvents.PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1, 0.5f);
        }
    }



    @Override
    public void animateItemFirstPerson(LivingEntity entity, ItemStack stack, InteractionHand hand, PoseStack poseStack, float partialTicks, float pitch, float attackAnim, float handHeight) {
        int isLeftHanded = entity.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
        int isOffhand = hand == InteractionHand.MAIN_HAND ? 1 : -1;

        float timeLeft = stack.getUseDuration() - entity.getUseItemRemainingTicks();

        if (shielding) {
            poseStack.translate(isLeftHanded * isOffhand * (-0.2 + (timeLeft > 20 ? -0.1 : 0)), 0.15, 0);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(isLeftHanded * isOffhand * 80));
        }
    }

    private <T extends LivingEntity> void shieldPose(HumanoidModel<T> model, boolean leftHand) {
        ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
        int isLeftHand = leftHand ? -1 : 1;

        mainHand.yRot = (float) Math.toRadians(isLeftHand * -30);
        mainHand.xRot = (float) Math.toRadians(-90);
        mainHand.zRot = (float) Math.toRadians(isLeftHand * 45);
    }

    private <T extends LivingEntity> void slashAnim(HumanoidModel<T> model, boolean leftHand, int slashAnimTick) {
        ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
        int isLeftHand = leftHand ? -1 : 1;

        mainHand.yRot = (float) Math.toRadians(isLeftHand * -30);
        mainHand.xRot = (float) Math.toRadians(-80);
        mainHand.zRot = (float) Math.toRadians(isLeftHand * 45);

        System.out.println(slashAnimTick);

        mainHand.xRot += (float) Math.toRadians(isLeftHand * Math.min(Math.log(Math.exp(100-slashAnimTick)) * 5, 90));
    }

    @Override
    public <T extends LivingEntity> boolean poseRightArm(ItemStack itemStack, HumanoidModel<T> humanoidModel, T entity, HumanoidArm humanoidArm, DualWeildState dualWeildState) {
        if (this.shielding) {
            this.shieldPose(humanoidModel, false);
            return true;
        }

        if (this.slashAnimCounter > 0) {
            this.slashAnim(humanoidModel, false, this.slashAnimCounter);
            this.slashAnimCounter--;
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> boolean poseLeftArm(ItemStack itemStack, HumanoidModel<T> humanoidModel, T entity, HumanoidArm humanoidArm, DualWeildState dualWeildState) {
        if (this.shielding) {
            this.shieldPose(humanoidModel, true);
            return true;
        }

        if (this.slashAnimCounter > 0) {
            this.slashAnim(humanoidModel, true, this.slashAnimCounter);
            this.slashAnimCounter--;
        }
        return false;
    }
}
