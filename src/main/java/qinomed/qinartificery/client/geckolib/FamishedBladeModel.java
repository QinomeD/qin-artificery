package qinomed.qinartificery.client.geckolib;

import net.minecraft.resources.ResourceLocation;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.item.weapons.FamishedBladeItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class FamishedBladeModel extends AnimatedGeoModel<FamishedBladeItem> {
    protected static final ResourceLocation MODEL = new ResourceLocation(QinArtificery.MODID, "geo/item/famished_blade.geo.json");
    protected static final ResourceLocation TEXTURE = new ResourceLocation(QinArtificery.MODID, "textures/item/famished_blade.png");
    protected static final ResourceLocation ANIMATION = new ResourceLocation(QinArtificery.MODID, "animations/item/famished_blade.animation.json");

    @Override
    public ResourceLocation getModelResource(FamishedBladeItem famishedBladeItem) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(FamishedBladeItem famishedBladeItem) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(FamishedBladeItem famishedBladeItem) {
        return ANIMATION;
    }
}
