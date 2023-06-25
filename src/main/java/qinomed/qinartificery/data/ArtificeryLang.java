package qinomed.qinartificery.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.StringUtils;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.registry.ItemRegistry;

public class ArtificeryLang extends LanguageProvider {
    public ArtificeryLang(DataGenerator gen, String locale) {
        super(gen, QinArtificery.MODID, locale);
    }

    @Override
    protected void addTranslations() {
        this.add("sounds.qinartificery.zap_explosion", "Discharge");
        this.add("itemGroup.artificeryTab", "Qin's Artificery");

        for (RegistryObject<Item> item : ItemRegistry.ITEMS.getEntries())
            this.add(item);
    }

    private void add(RegistryObject<Item> item) {
        this.add(item.get(), toTitleCase(item.get().toString()));
    }

    private static String toTitleCase(String str) {
        String[] words = str.split("_");
        StringBuilder builder = new StringBuilder();
        for (String s : words) {
            s = StringUtils.capitalize(s);
            if (!builder.isEmpty())
                builder.append(" ");
            builder.append(s);
        }
        return builder.toString();
    }
}
