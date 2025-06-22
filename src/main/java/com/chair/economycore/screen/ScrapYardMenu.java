package com.chair.economycore.screen;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.google.common.collect.Maps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ScrapYardMenu extends AbstractContainerMenu {
    protected final Player player;
    protected final Container sellContainer;
    
    private static final Map<Item, Long> SCRAP_PRICES = Maps.newHashMap();

    static {
        SCRAP_PRICES.put(Items.COBBLESTONE, 1L);
        SCRAP_PRICES.put(Items.DIRT, 1L);
        SCRAP_PRICES.put(Items.SAND, 2L);
        SCRAP_PRICES.put(Items.GRAVEL, 2L);
        SCRAP_PRICES.put(Items.ROTTEN_FLESH, 3L);
    }

    public ScrapYardMenu(int pContainerId, Inventory inv) { this(pContainerId, inv, new SimpleContainerData(1)); }
    public ScrapYardMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) { this(pContainerId, inv); }
    public ScrapYardMenu(int pContainerId, Inventory playerInventory, ContainerData data) {
        super(ModMenuTypes.SCRAP_YARD_MENU.get(), pContainerId);
        checkContainerSize(playerInventory, 1);
        this.player = playerInventory.player;
        this.sellContainer = new SimpleContainer(1);
        
        // 【核心修正】使用與生成器完全一致的標準座標
        this.addSlot(new Slot(this.sellContainer, 0, 80, 36));

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        this.addDataSlots(data);
    }
    
    public void performSell(ServerPlayer pPlayer) {
        ItemStack itemInSlot = this.sellContainer.getItem(0);
        if (SCRAP_PRICES.containsKey(itemInSlot.getItem())) {
            int itemCount = itemInSlot.getCount();
            long unitPrice = SCRAP_PRICES.get(itemInSlot.getItem());
            long totalPrice = itemCount * unitPrice;

            pPlayer.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
                money.addMoney(totalPrice);
                ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), pPlayer);
                pPlayer.sendSystemMessage(Component.literal("成功出售 " + itemCount + " 個 " + itemInSlot.getHoverName().getString() + "，獲得了 " + totalPrice + " 元。"));
            });
            this.sellContainer.setItem(0, ItemStack.EMPTY);
        } else if (!itemInSlot.isEmpty()) {
            pPlayer.sendSystemMessage(Component.literal("廢品回收員對這個不感興趣。"));
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
                if (SCRAP_PRICES.containsKey(itemstack1.getItem())) {
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
            // 【核心修正】使用標準座標
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            // 【核心修正】使用標準座標
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}