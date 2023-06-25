package qinomed.qinartificery.util;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.registry.ItemRegistry;

public class ArtificeryItemProperties {
    public static void addProperties() {
        addChargeProperty(ItemRegistry.NEPHRITE_BOMB.get());
        addChargingProperty(ItemRegistry.NEPHRITE_BOMB.get());

        addChargingProperty(ItemRegistry.HUNGERING_SHIELD.get());
    }

    private static void addChargeProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation(QinArtificery.MODID, "charge"),
                (stack, level, entity, ohItsSeed) -> {
                    if (entity != null)
                        return entity.getUseItem() != stack ? 0.0f : (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20f;
                    else
                        return 0.0f;
                });
    }

    private static void addChargingProperty(Item item) {
        ItemProperties.register(item, new ResourceLocation(QinArtificery.MODID, "charging"),
                (stack, level, entity, ohItsSeed) -> {
                    if (entity != null)
                        return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0f : 0.0f;
                    else
                        return 0.0f;
                });
    }
}
