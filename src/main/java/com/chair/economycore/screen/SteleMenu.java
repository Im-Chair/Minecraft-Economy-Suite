package com.chair.economycore.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class SteleMenu extends AbstractContainerMenu {
    // 這個建構子是給客戶端和MenuProvider使用的
    public SteleMenu(int pContainerId, Inventory inv) {
        super(ModMenuTypes.STELE_MENU.get(), pContainerId);
    }

    // 這個建構子是給伺服器用於網路同步的，保留是好習慣
    public SteleMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv);
    }

    // 【筆記】判斷玩家是否還能與GUI互動（例如距離太遠就自動關閉）。我們暫時讓它永遠有效。
    @Override
    public boolean stillValid(Player pPlayer) {
        return true;
    }

    // 【筆記】處理Shift點擊時的物品轉移，我們的GUI沒有物品欄，所以讓它為空。
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
