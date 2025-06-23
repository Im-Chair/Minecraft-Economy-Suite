package com.chair.economycore.core;

import com.chair.economycore.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

public class ArtisanShopRegistry {

    // 我們也將 ShopItem record 移到這裡，作為一個公開的資料結構
    public record ShopItem(ItemStack item, long price, String description) {}

    // 這是天穹工匠的商品列表
    public static final List<ShopItem> SHOP_ITEMS = List.of(
            new ShopItem(new ItemStack(ModItems.BOUNDARY_STONE.get()), 500L, "一個充滿魔力的石頭，似乎能標記位置。"),
            new ShopItem(new ItemStack(Items.COMPASS), 250L, "指向世界的出生點。"),
            new ShopItem(new ItemStack(Items.CLOCK), 250L, "顯示當前的時間。")
            // 未來可以輕鬆地在這裡新增更多商品
    );
}