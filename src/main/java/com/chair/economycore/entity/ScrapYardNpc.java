package com.chair.economycore.entity;

import com.chair.economycore.screen.ScrapYardMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 【核心修正】讓 NPC 類別實作 MenuProvider 介面
public class ScrapYardNpc extends PathfinderMob implements MenuProvider {

    public ScrapYardNpc(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // 【核心修正】修改互動邏輯，從直接給錢改為開啟 GUI
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                // NetworkHooks 會自動處理客戶端與伺服器間的同步
                // 第二個參數 this 代表將 NPC 自身作為 MenuProvider
                NetworkHooks.openScreen(serverPlayer, this, this.blockPosition());
            }
        }
        return InteractionResult.SUCCESS;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D);
    }
    
    @Override
    protected void registerGoals() {
        // 留空，暫不添加任何AI行為
    }

    // ---- MenuProvider 介面需要實作的方法 ----

    @NotNull
    @Override
    public Component getDisplayName() {
        // 這個名稱將會顯示在 GUI 的左上角
        return Component.literal("廢品回收站");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        // 告訴遊戲當這個 MenuProvider 被呼叫時，要創建哪個 Menu 的實例
        return new ScrapYardMenu(pContainerId, pPlayerInventory);
    }
}