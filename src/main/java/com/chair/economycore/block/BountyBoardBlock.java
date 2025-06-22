package com.chair.economycore.block;

import com.chair.economycore.screen.BountyBoardMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 【核心修正】讓方塊實作 MenuProvider 介面
public class BountyBoardBlock extends Block implements MenuProvider {

    public BountyBoardBlock(Properties pProperties) {
        super(pProperties);
    }

    // 【核心修正】修改右鍵互動邏輯
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                // 這個方法會為玩家打開一個與此方塊關聯的 GUI
                NetworkHooks.openScreen(serverPlayer, this, pPos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    // --- MenuProvider 介面需要實作的方法 ---

    @NotNull
    @Override
    public Component getDisplayName() {
        // GUI 的標題
        return Component.literal("懸賞板");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        // 告訴遊戲要創建哪個 Menu 的實例
        return new BountyBoardMenu(pContainerId, pPlayerInventory, null);
    }
}