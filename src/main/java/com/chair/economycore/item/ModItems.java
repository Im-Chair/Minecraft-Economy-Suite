package com.chair.economycore.item;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EconomyCore.MODID);

    // 物品：古代石碑
    public static final RegistryObject<Item> ANCIENT_STELE = ITEMS.register("ancient_stele",
            () -> new AncientSteleItem(new Item.Properties()));

    // 【新增】物品：廢品回收員生成蛋
    public static final RegistryObject<Item> SCRAP_YARD_NPC_SPAWN_EGG = ITEMS.register("scrap_yard_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCRAP_YARD_NPC, 0x555555, 0xAAAAAA, new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}