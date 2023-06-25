package qinomed.qinartificery.item.trinkets;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;

import java.util.Arrays;

public class ExclusiveCurioItem extends BaseCurioItem{
    public ExclusiveCurioItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        return CuriosApi.getCuriosHelper().findCurios(entity, item -> Arrays.stream(this.getExclusiveFromItems()).findAny().isPresent()).isEmpty();
    }

    public Item[] getExclusiveFromItems() {
        return new Item[]{};
    }
}
