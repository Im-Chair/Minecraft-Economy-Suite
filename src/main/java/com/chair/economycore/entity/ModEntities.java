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

    // 註冊我們的「廢品回收員」NPC
    public static final RegistryObject<EntityType<ScrapYardNpc>> SCRAP_YARD_NPC =
            ENTITY_TYPES.register("scrap_yard_npc",
                    () -> EntityType.Builder.of(ScrapYardNpc::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.8f) // 設定實體的碰撞箱大小 (寬, 高)，這裡用的是玩家的大小
                            .build("scrap_yard_npc"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}