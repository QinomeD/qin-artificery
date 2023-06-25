package qinomed.qinartificery.item.weapons;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class AttunementItem extends SwordItem {
    public AttunementItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, Properties pProperties) {
        super(pTier, pAttackDamageModifier, pAttackSpeedModifier, pProperties);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        Level level = entity.getLevel();
        CompoundTag tag = stack.getTag();
        EntityType<?> lastTarget = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(stack.getTag().getString("LastTarget")));

        if (entity.getType() == lastTarget && player.getAttackStrengthScale(0) == 1) {
            var bonus = tag.contains("BonusDamage") ? tag.getInt("BonusDamage") : 0;
            if (bonus < 8)
                bonus++;
            entity.hurt(DamageSource.playerAttack(player).bypassArmor(), bonus);
            tag.putInt("BonusDamage", bonus);

            /*
            if (LDLib.isClient()) {
                spawnEffect(level, entity);
            }

             */

            level.playSound(null, player.getOnPos().above(2), SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS, 4, 1);
        } else {
            stack.getTag().putInt("BonusDamage", 0);
        }

        stack.getTag().putString("LastTarget", ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString());
        
        return super.onLeftClickEntity(stack, player, entity);
    }

    /*
    @OnlyIn(Dist.CLIENT)
    private void spawnEffect(Level level, Entity entity) {
        FX effect = FXHelper.getFX(new ResourceLocation(QinArtificery.MODID, "attunement_fx"));
        var emitter = effect.emitters().get(0).copy();
        emitter.emmitToLevel(level, entity.getX(), entity.getY() + entity.getBbHeight()/2, entity.getZ());
    }

     */
}
