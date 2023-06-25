package qinomed.qinartificery.client.geckolib;

import qinomed.qinartificery.item.weapons.FamishedBladeItem;
import qinomed.qinartificery.item.weapons.HungeringShieldItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class HungeringShieldRenderer extends GeoItemRenderer<HungeringShieldItem> {
    public HungeringShieldRenderer() {
        super(new HungeringShieldModel());
    }
}
