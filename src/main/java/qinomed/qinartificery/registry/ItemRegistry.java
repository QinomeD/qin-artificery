package qinomed.qinartificery.registry;

import com.github.elenterius.biomancy.init.ModRarities;
import com.github.elenterius.biomancy.init.ModTiers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.item.trinkets.BaseCurioItem;
import qinomed.qinartificery.item.trinkets.MuscleCompositeItem;
import qinomed.qinartificery.item.trinkets.NephritePendantItem;
import qinomed.qinartificery.item.trinkets.RottingCompositeItem;
import qinomed.qinartificery.item.weapons.*;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, QinArtificery.MODID);
    public static final Supplier<Item.Properties> BASIC = () -> new Item.Properties().tab(QinArtificery.ARTIFICERY_TAB);

    public static final RegistryObject<Item>
            HALITE_BATON = addItem("halite_baton", () -> new SwordItem(Tiers.STONE, 3, -2.4f, BASIC.get())),
            NEPHRITE_BOMB = addItem("nephrite_bomb", () -> new NephriteBombItem(BASIC.get())),
            NEPHRITE_PENDANT = addItem("nephrite_pendant", () -> new NephritePendantItem(BASIC.get())),
            ATTUNEMENT = addItem("attunement", () -> new AttunementItem(Tiers.IRON, 3, -2.4f, BASIC.get())),
            WOODEN_CROSS = addCurio("wooden_cross"),
            LEAD_CROSS = addCurio("lead_cross"),
            ROTTING_COMPOSITE = addItem("rotting_composite", () -> new RottingCompositeItem(BASIC.get())),
            MUSCLE_COMPOSITE = addItem("muscle_composite", () -> new MuscleCompositeItem(BASIC.get())),
            CHAINED_CLAYMORE = addItem("chained_claymore", () -> new ChainedClaymoreItem(Tiers.IRON, 5, -3f, BASIC.get())),
            TEST_WEAPON = addItem("test_weapon", () -> new TestEffectItem(Tiers.NETHERITE, 5, -3, BASIC.get())),
            FAMISHED_BLADE = addItem("famished_blade", () -> new FamishedBladeItem(ModTiers.BIOFLESH, 7, 1.6f, 250, BASIC.get().rarity(ModRarities.VERY_RARE))),
            HUNGERING_SHIELD = addItem("hungering_shield", () -> new HungeringShieldItem(BASIC.get().rarity(ModRarities.VERY_RARE))),
            ENGRAVED_RUNE = addItem("engraved_rune");

    private static RegistryObject<Item> addItem(String name) {
        return addItem(name, () -> new Item(BASIC.get()));
    }

    private static RegistryObject<Item> addItem(String name, Supplier<? extends Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    private static RegistryObject<Item> addCurio(String name) {
        return ITEMS.register(name, () -> new BaseCurioItem(BASIC.get()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
