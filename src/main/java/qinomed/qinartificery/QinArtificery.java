package qinomed.qinartificery;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import qinomed.qinartificery.data.ArtificeryItemModels;
import qinomed.qinartificery.data.ArtificeryLang;
import qinomed.qinartificery.network.ModMessages;
import qinomed.qinartificery.registry.EntityTypesRegistry;
import qinomed.qinartificery.registry.ItemRegistry;
import qinomed.qinartificery.registry.SoundRegistry;
import software.bernie.geckolib3.GeckoLib;
import top.theillusivec4.curios.api.SlotTypeMessage;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(QinArtificery.MODID)
public class QinArtificery {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "qinartificery";

    public static final CreativeModeTab ARTIFICERY_TAB = new CreativeModeTab("artificeryTab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ItemRegistry.HALITE_BATON.get());
        }
    };

    public QinArtificery() {
        GeckoLib.initialize();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ItemRegistry.register(modEventBus);
        EntityTypesRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);

        modEventBus.addListener(WhyDoINeedYou::gatherData);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register();
        });
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("body").size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("necklace").size(1).build());
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("charm").size(2).build());
    }

    public static ResourceLocation modPath(String s) {
        return new ResourceLocation(MODID, s);
    }

    public static class WhyDoINeedYou {
        public static void gatherData(GatherDataEvent event) {
            DataGenerator generator = event.getGenerator();
            generator.addProvider(event.includeServer(), new ArtificeryItemModels(generator, event.getExistingFileHelper()));
            generator.addProvider(event.includeClient(), new ArtificeryLang(generator, "en_us"));
        }
    }
}
