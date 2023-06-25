package qinomed.qinartificery.client.geckolib;

import qinomed.qinartificery.item.weapons.FamishedBladeItem;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class FamishedBladeRenderer extends GeoItemRenderer<FamishedBladeItem> {
    public FamishedBladeRenderer() {
        super(new FamishedBladeModel());
    }
}
