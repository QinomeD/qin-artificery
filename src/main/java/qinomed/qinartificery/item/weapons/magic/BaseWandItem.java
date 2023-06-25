package qinomed.qinartificery.item.weapons.magic;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import qinomed.qinartificery.data.ArtificeryTags;

import java.util.function.Predicate;

public class BaseWandItem extends ProjectileWeaponItem {
    public BaseWandItem(Properties pProperties) {
        super(pProperties);
    }

    public void onProjectileHit(LivingEntity target, Player caster) {

    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return item -> item.is(ArtificeryTags.RUNES);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}
