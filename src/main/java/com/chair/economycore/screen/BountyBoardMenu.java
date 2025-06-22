package com.chair.economycore.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BountyBoardMenu extends AbstractContainerMenu {

    // 【核心修正】根據 220 寬度，計算出置中的 X 座標
    private static final int PLAYER_INV_X = (220 - 162) / 2; // 29
    private static final int PLAYER_INV_Y = 139;
    private static final int PLAYER_HOTBAR_Y = 197;

    protected BountyBoardMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInventory) {
        super(pMenuType, pContainerId);
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public BountyBoardMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(ModMenuTypes.BOUNTY_BOARD_MENU.get(), pContainerId, playerInventory);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) { return true; }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) { return ItemStack.EMPTY; }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, PLAYER_INV_X + j * 18, PLAYER_INV_Y + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, PLAYER_INV_X + i * 18, PLAYER_HOTBAR_Y));
        }
    }
}