package qinomed.qinartificery.item.trinkets;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public class BaseCurioItem extends Item implements ICurioItem {
    public BaseCurioItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canRightClickEquip(ItemStack stack) {
        return true;
    }
}
