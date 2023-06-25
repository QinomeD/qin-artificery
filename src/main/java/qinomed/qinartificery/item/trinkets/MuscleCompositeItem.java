package qinomed.qinartificery.item.trinkets;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;

import java.util.UUID;

public class MuscleCompositeItem extends BaseCurioItem{
    public MuscleCompositeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        map.put(Attributes.ATTACK_SPEED, new AttributeModifier(uuid, "Curio attack speed boost", 0.1d, AttributeModifier.Operation.MULTIPLY_TOTAL));
        map.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(uuid, "Curio speed boost", 0.3d, AttributeModifier.Operation.MULTIPLY_TOTAL));
        map.put(Attributes.ARMOR, new AttributeModifier(uuid, "Curio armor boost", 3, AttributeModifier.Operation.ADDITION));
        return map;
    }
}
