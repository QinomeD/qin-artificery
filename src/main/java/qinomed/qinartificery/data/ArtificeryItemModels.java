package qinomed.qinartificery.data;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import qinomed.qinartificery.QinArtificery;

import static qinomed.qinartificery.QinArtificery.modPath;
import static qinomed.qinartificery.registry.ItemRegistry.*;

public class ArtificeryItemModels extends ItemModelProvider {

    public ArtificeryItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, QinArtificery.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        handheldItem(HALITE_BATON);
        nephriteBombItem(NEPHRITE_BOMB);
        generatedItem(NEPHRITE_PENDANT);
        handheldItem(ATTUNEMENT);
        generatedItem(WOODEN_CROSS);
        generatedItem(LEAD_CROSS);
        generatedItem(ROTTING_COMPOSITE);
        generatedItem(MUSCLE_COMPOSITE);
        handheldItem(CHAINED_CLAYMORE);
        handheldItem(TEST_WEAPON);
    }

    private static final ResourceLocation GENERATED = new ResourceLocation("item/generated");
    private static final ResourceLocation HANDHELD = new ResourceLocation("item/handheld");

    private void nephriteBombItem(RegistryObject<Item> i) {
        String name = Registry.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, HANDHELD).texture("layer0", modPath("item/" + name + "_2"));
        getBuilder(name).override()
                .predicate(new ResourceLocation(QinArtificery.MODID, "charging"), 0f)
                .model(withExistingParent(name + "_off", HANDHELD).texture("layer0", modPath("item/" + name))).end();
        getBuilder(name).override()
                .predicate(new ResourceLocation(QinArtificery.MODID, "charging"), 1f)
                .model(withExistingParent(name + "_0", HANDHELD).texture("layer0", modPath("item/" + name + "_0"))).end();
        getBuilder(name).override()
                .predicate(new ResourceLocation(QinArtificery.MODID, "charge"), 0.65f).predicate(new ResourceLocation(QinArtificery.MODID, "charging"), 1f)
                .model(withExistingParent(name + "_1", HANDHELD).texture("layer0", modPath("item/" + name + "_1"))).end();
        getBuilder(name).override()
                .predicate(new ResourceLocation(QinArtificery.MODID, "charge"), 0.9f).predicate(new ResourceLocation(QinArtificery.MODID, "charging"), 1f)
                .model(withExistingParent(name + "_2", HANDHELD).texture("layer0", modPath("item/" + name + "_2"))).end();
    }

    private void handheldItem(RegistryObject<Item> i) {
        String name = Registry.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, HANDHELD).texture("layer0", modPath("item/" + name));
    }

    private void generatedItem(RegistryObject<Item> i) {
        String name = Registry.ITEM.getKey(i.get()).getPath();
        withExistingParent(name, GENERATED).texture("layer0", modPath("item/" + name));
    }
}
