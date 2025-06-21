package com.chair.economycore.item;

// 【注意】確保以下所有import都已存在
import com.chair.economycore.screen.SteleMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class AncientSteleItem extends Item {

    public AncientSteleItem(Properties pProperties) {
        super(pProperties);
    }

    // 【重點】這是最終的、正確的 use 方法
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        // 確保邏輯只在伺服器端執行
        if (!pLevel.isClientSide() && pPlayer instanceof ServerPlayer serverPlayer) {

            // 【核心修正】我們不再發送聊天訊息，而是直接呼叫Forge的輔助方法來打開GUI。
            // 這會創建一個Menu的提供者，並告訴伺服器為該玩家打開這個GUI容器。
            NetworkHooks.openScreen(serverPlayer, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    // 這個名字會顯示在GUI的左上角
                    return Component.literal("古代石碑");
                }

                @Override
public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
                    // 這裡，我們創建了我們GUI後台邏輯(Menu)的實例
                    return new SteleMenu(pContainerId, pPlayerInventory);
                }
            });
        }

        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }
}
