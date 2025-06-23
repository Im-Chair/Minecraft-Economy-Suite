package com.chair.economycore.screen;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.core.ScrapYardRegistry;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ScrapYardMenu extends AbstractContainerMenu {
    protected final Player player;
    protected final Container sellContainer;

    public ScrapYardMenu(int pContainerId, Inventory inv, ContainerData data) {
        super(ModMenuTypes.SCRAP_YARD_MENU.get(), pContainerId);
        checkContainerSize(inv, 1);
        this.player = inv.player;
        this.sellContainer = new SimpleContainer(1);
        this.addSlot(new Slot(this.sellContainer, 0, 80, 36));
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.addDataSlots(data);
    }
    
    public ScrapYardMenu(int pContainerId, Inventory inv) {
        this(pContainerId, inv, new SimpleContainerData(1));
    }

    public ScrapYardMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv);
    }
    
    // 【核心修正】新增 performSell 方法，取代舊的 clickMenuButton
    public void performSell(ServerPlayer pPlayer) {
        ItemStack itemInSlot = this.sellContainer.getItem(0);

        if (ScrapYardRegistry.SCRAP_PRICES.containsKey(itemInSlot.getItem())) {
            int itemCount = itemInSlot.getCount();
            long unitPrice = ScrapYardRegistry.SCRAP_PRICES.get(itemInSlot.getItem());
            long totalPrice = itemCount * unitPrice;

            pPlayer.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
                money.addMoney(totalPrice);
                ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), pPlayer);
                pPlayer.sendSystemMessage(Component.literal("成功出售 " + itemCount + " 個 " + itemInSlot.getHoverName().getString() + "，獲得了 " + totalPrice + " 元。"));
            });
            this.sellContainer.setItem(0, ItemStack.EMPTY);
            
        } else {
            if (!itemInSlot.isEmpty()) {
                pPlayer.sendSystemMessage(Component.literal("廢品回收員對這個不感興趣。"));
            }
        }
    }
    
    @Override
    public void removed(@NotNull Player pPlayer) {
        super.removed(pPlayer);
        this.clearContainer(pPlayer, this.sellContainer);
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) { return true; }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            final int SELL_SLOT_INDEX = 0;
            final int PLAYER_INVENTORY_START_INDEX = 1;
            final int PLAYER_HOTBAR_END_INDEX = 37;

            if (pIndex == SELL_SLOT_INDEX) {
                if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START_INDEX, PLAYER_HOTBAR_END_INDEX, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(itemstack1, itemstack);
            } 
            else if (pIndex >= PLAYER_INVENTORY_START_INDEX) {
                if (ScrapYardRegistry.SCRAP_PRICES.containsKey(itemstack1.getItem())) {
                    if (!this.moveItemStackTo(itemstack1, SELL_SLOT_INDEX, SELL_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                     return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, itemstack1);
        }
        return itemstack;
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