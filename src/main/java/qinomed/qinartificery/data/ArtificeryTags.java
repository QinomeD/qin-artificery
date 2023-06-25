package qinomed.qinartificery.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.registry.ItemRegistry;

public class ArtificeryTags extends ItemTagsProvider {
    public static final TagKey<Item> RUNES = ItemTags.create(new ResourceLocation(QinArtificery.MODID, "runes"));

    public ArtificeryTags(DataGenerator pGenerator, BlockTagsProvider pBlockTagsProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pGenerator, pBlockTagsProvider, QinArtificery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(RUNES).add(
                ItemRegistry.ENGRAVED_RUNE.get()
        );
    }
}
