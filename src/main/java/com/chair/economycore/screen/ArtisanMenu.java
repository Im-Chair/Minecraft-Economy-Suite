package com.chair.economycore.screen;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.core.ArtisanShopRegistry; // 【新增】
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class ArtisanMenu extends AbstractContainerMenu {

    private final List<ArtisanShopRegistry.ShopItem> shopItems;

    public ArtisanMenu(int pContainerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(ModMenuTypes.ARTISAN_MENU.get(), pContainerId, playerInventory);
    }

    protected ArtisanMenu(MenuType<?> pMenuType, int pContainerId, Inventory playerInventory) {
        super(pMenuType, pContainerId);
        
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
        
        // 【核心修正】從 ArtisanShopRegistry 獲取商品列表
        this.shopItems = ArtisanShopRegistry.SHOP_ITEMS;
    }
    
    public void performPurchase(ServerPlayer player, int itemIndex) {
        if (itemIndex >= 0 && itemIndex < this.shopItems.size()) {
            ArtisanShopRegistry.ShopItem selectedItem = this.shopItems.get(itemIndex);
            long price = selectedItem.price();
            
            player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
                if (money.getMoney() >= price) {
                    money.removeMoney(price);
                    ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
                    player.getInventory().add(selectedItem.item().copy());
                    player.sendSystemMessage(Component.literal("購買成功！"));
                } else {
                    player.sendSystemMessage(Component.literal("你的錢不夠！"));
                }
            });
        }
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) { return true; }

    @NotNull
    @Override
    public ItemStack quickMoveStack(@NotNull Player pPlayer, int pIndex) { return ItemStack.EMPTY; }

    // --- 輔助方法，用於繪製玩家物品欄 ---
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                // 【核心修正】在 x 座標中加上 `j * 18` 來讓格子正確排列
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 139 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            // 【核心修正】在 x 座標中加上 `i * 18` 來讓格子正確排列
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 197));
        }
    }
}