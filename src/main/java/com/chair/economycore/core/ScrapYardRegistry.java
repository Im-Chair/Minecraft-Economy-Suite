package com.chair.economycore.core;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.Map;

public class ScrapYardRegistry {

    // 廢品回收價目表 (物品 -> 收購價格)
    public static final Map<Item, Long> SCRAP_PRICES;

    static {
        ImmutableMap.Builder<Item, Long> builder = ImmutableMap.builder();

        builder.put(Items.COBBLESTONE, 1L);
        builder.put(Items.DIRT, 1L);
        builder.put(Items.SAND, 2L);
        builder.put(Items.GRAVEL, 2L);
        builder.put(Items.ROTTEN_FLESH, 3L);
        // 未來可以輕鬆地在這裡新增更多可回收的廢品
        
        SCRAP_PRICES = builder.build();
    }
}