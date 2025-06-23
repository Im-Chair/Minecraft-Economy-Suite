package com.chair.economycore.screen;

// 【新增】引入我們所有需要的類別
import com.chair.economycore.core.AgeManager;
import com.chair.economycore.data.WorldProgressionData;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.WorldProgressionSyncS2CPacket;
import com.chair.economycore.util.Era;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SteleMenu extends AbstractContainerMenu {

    // 主要的建構子
    public SteleMenu(int pContainerId, Inventory playerInventory) {
        super(ModMenuTypes.STELE_MENU.get(), pContainerId);
        
        // 【新增】在此處添加資料同步邏輯
        Player player = playerInventory.player;
        Level level = player.level();

        // 檢查是否在伺服器端，並且玩家是 ServerPlayer 實例
        if(!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            // 從伺服器獲取世界進程數據
            WorldProgressionData data = WorldProgressionData.get(serverPlayer.serverLevel());
            Era currentEra = data.getCurrentEra();
            long currentCp = data.getCivilizationPoints();
            Era nextEra = currentEra.getNext();
            // 使用 AgeManager 獲取動態計算後的需求CP
            long requiredCp = AgeManager.getScaledRequiredCp(serverPlayer.getServer(), nextEra);

            // 建立封包並透過網路發送給打開介面的玩家
            ModMessages.sendToPlayer(new WorldProgressionSyncS2CPacket(currentEra, currentCp, nextEra, requiredCp), serverPlayer);
        }
        
        // 原有的繪製玩家物品欄邏輯
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    
    // Forge 網路系統需要的建構子
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