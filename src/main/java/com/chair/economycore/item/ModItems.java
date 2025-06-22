package com.chair.economycore.item;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
// ... 省略 package 和其他 import ...
import com.chair.economycore.block.ModBlocks; // 【新增】
import net.minecraft.world.item.BlockItem; // 【新增】

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EconomyCore.MODID);

    public static final RegistryObject<Item> ANCIENT_STELE = ITEMS.register("ancient_stele",
            () -> new AncientSteleItem(new Item.Properties()));

    public static final RegistryObject<Item> BOUNDARY_STONE = ITEMS.register("boundary_stone",
            () -> new BoundaryStoneItem(new Item.Properties()));

    public static final RegistryObject<Item> SCRAP_YARD_NPC_SPAWN_EGG = ITEMS.register("scrap_yard_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SCRAP_YARD_NPC, 0x552255, 0xAA00AA, new Item.Properties()));
            
    // 【新增】天穹工匠生成蛋 (我們為它選一個高貴的藍金色)
    public static final RegistryObject<Item> AETHERIUM_ARTISAN_NPC_SPAWN_EGG = ITEMS.register("aetherium_artisan_npc_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.AETHERIUM_ARTISAN_NPC, 0x2A4D85, 0xFFD700, new Item.Properties()));
    // 【新增】為我們的懸賞板方塊，創建一個對應的 BlockItem
    public static final RegistryObject<Item> BOUNTY_BOARD_ITEM = ITEMS.register("bounty_board",
            () -> new BlockItem(ModBlocks.BOUNTY_BOARD.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}