package qinomed.qinartificery.client.geckolib;

import net.minecraft.resources.ResourceLocation;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.item.weapons.FamishedBladeItem;
import qinomed.qinartificery.item.weapons.HungeringShieldItem;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HungeringShieldModel extends AnimatedGeoModel<HungeringShieldItem> {
    protected static final ResourceLocation MODEL = new ResourceLocation(QinArtificery.MODID, "geo/item/hungering_shield.geo.json");
    protected static final ResourceLocation TEXTURE = new ResourceLocation(QinArtificery.MODID, "textures/item/hungering_shield.png");
    protected static final ResourceLocation ANIMATION = new ResourceLocation(QinArtificery.MODID, "animations/item/hungering_shield.animation.json");

    @Override
    public ResourceLocation getModelResource(HungeringShieldItem famishedBladeItem) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(HungeringShieldItem famishedBladeItem) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(HungeringShieldItem famishedBladeItem) {
        return ANIMATION;
    }
}
