package com.chair.economycore.entity;

import com.chair.economycore.EconomyCore;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EconomyCore.MODID);

    // 已有的：廢品回收員
    public static final RegistryObject<EntityType<ScrapYardNpc>> SCRAP_YARD_NPC =
            ENTITY_TYPES.register("scrap_yard_npc",
                    () -> EntityType.Builder.of(ScrapYardNpc::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f)
                            .build("scrap_yard_npc"));

    // 【新增】註冊天穹工匠
    public static final RegistryObject<EntityType<AetheriumArtisanNpc>> AETHERIUM_ARTISAN_NPC =
            ENTITY_TYPES.register("aetherium_artisan_npc",
                    () -> EntityType.Builder.of(AetheriumArtisanNpc::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f)
                            .build("aetherium_artisan_npc"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}