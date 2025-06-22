package com.chair.economycore.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SteleMenu extends AbstractContainerMenu {

    // 主要的建構子
    public SteleMenu(int pContainerId, Inventory playerInventory) {
        super(ModMenuTypes.STELE_MENU.get(), pContainerId);
        
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    
    // 【新增】補上 Forge 網路系統需要的建構子，解決編譯錯誤
    public SteleMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(pContainerId, playerInventory);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}