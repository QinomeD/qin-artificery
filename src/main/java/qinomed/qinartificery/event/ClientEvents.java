package qinomed.qinartificery.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.registry.ItemRegistry;

@Mod.EventBusSubscriber(modid = QinArtificery.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents {

    @SubscribeEvent
    public static void fovModifier(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();
        if (player.isUsingItem() && player.getUseItem().getItem() == ItemRegistry.NEPHRITE_BOMB.get()) {
            float modifier = player.getTicksUsingItem() / 20f;
            if (modifier > 1f) {
                modifier = 1.0f;
            } else {
                modifier *= modifier;
            }
            event.setNewFovModifier(event.getFovModifier() * 1.0f - modifier * 0.15f);
        }
    }
}
