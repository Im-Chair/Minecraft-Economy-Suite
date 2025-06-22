package com.chair.economycore.entity;

import com.chair.economycore.screen.ArtisanMenu;
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

// 【核心修正】實作 MenuProvider
public class AetheriumArtisanNpc extends PathfinderMob implements MenuProvider {

    public AetheriumArtisanNpc(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) { super(pEntityType, pLevel); }

    // 【核心修正】互動邏輯改為開啟 GUI
    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        if (!this.level().isClientSide()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                NetworkHooks.openScreen(serverPlayer, this, this.blockPosition());
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    public static AttributeSupplier.Builder createAttributes() { /* ... */ return PathfinderMob.createMobAttributes(); }

    // --- MenuProvider 介面需要實作的方法 ---
    @NotNull
    @Override
    public Component getDisplayName() {
        return Component.literal("天穹工匠");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        // 返回我們新的 ArtisanMenu
        return new ArtisanMenu(pContainerId, pPlayerInventory, null);
    }
}