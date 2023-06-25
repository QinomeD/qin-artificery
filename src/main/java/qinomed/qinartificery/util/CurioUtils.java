package qinomed.qinartificery.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.CuriosApi;

public class CurioUtils {

    public static boolean isEquipped(LivingEntity entity, Item item) {
        return !CuriosApi.getCuriosHelper().findCurios(entity, item).isEmpty();
    }
}
