package qinomed.qinartificery.event;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.client.renderer.NephriteBombRenderer;
import qinomed.qinartificery.registry.EntityTypesRegistry;
import qinomed.qinartificery.registry.ItemRegistry;
import qinomed.qinartificery.util.ArtificeryItemProperties;

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
            event.setNewFovModifier(event.getFovModifier() - modifier * 0.15f);
        }
    }

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ArtificeryItemProperties.addProperties();

        EntityRenderers.register(EntityTypesRegistry.NEPHRITE_BOMB_PROJECTILE.get(), NephriteBombRenderer::new);
    }

}
