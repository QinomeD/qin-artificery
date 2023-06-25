package qinomed.qinartificery.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import qinomed.qinartificery.QinArtificery;
import qinomed.qinartificery.entity.projectile.FlintShardProjectile;
import qinomed.qinartificery.entity.projectile.NephriteBombProjectile;

public class EntityTypesRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, QinArtificery.MODID);

    public static final RegistryObject<EntityType<NephriteBombProjectile>> NEPHRITE_BOMB_PROJECTILE =
            ENTITY_TYPES.register("nephrite_bomb",
                    () -> EntityType.Builder.of(NephriteBombProjectile::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(QinArtificery.MODID, "nephrite_bomb").toString()));

    public static final RegistryObject<EntityType<FlintShardProjectile>> FLINT_SHARD_PROJECTILE =
            ENTITY_TYPES.register("flint_shard",
                    () -> EntityType.Builder.of(FlintShardProjectile::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f)
                            .build(new ResourceLocation(QinArtificery.MODID, "flint_shard").toString()));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
