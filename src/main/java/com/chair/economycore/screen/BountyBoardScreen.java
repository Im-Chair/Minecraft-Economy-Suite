package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.client.ClientReputationData;
import com.chair.economycore.core.BountyRegistry;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.AcceptBountyC2SPacket;
import com.chair.economycore.network.packet.CompleteBountyC2SPacket;
import com.chair.economycore.util.AdventurerRank;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BountyBoardScreen extends AbstractContainerScreen<BountyBoardMenu> {
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(EconomyCore.MODID, "textures/gui/bounty_board_gui.png");

    private int currentPage = 0;
    private final int bountiesPerPage = 4;
    private int totalPages;
    // 【核心修正】將欄位名稱從 bounties 改為 displayableBounties
    private final List<BountyRegistry.Bounty> displayableBounties;

    public BountyBoardScreen(BountyBoardMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 220;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;

        // 根據玩家當前階級，過濾出所有可顯示的任務
        AdventurerRank playerRank = ClientReputationData.getPlayerRank();
        this.displayableBounties = BountyRegistry.BOUNTIES.values().stream()
                .filter(bounty -> bounty.requiredRank().ordinal() <= playerRank.ordinal())
                .collect(Collectors.toList());
        
        this.totalPages = (int) Math.ceil((double) this.displayableBounties.size() / this.bountiesPerPage);
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        int pageButtonY = y + 135;
        this.addRenderableWidget(Button.builder(Component.literal("<"), (button) -> {
            if (this.currentPage > 0) { this.currentPage--; this.rebuildWidgets(); }
        }).bounds(x + 10, pageButtonY, 15, 15).build()).active = this.currentPage > 0;

        this.addRenderableWidget(Button.builder(Component.literal(">"), (button) -> {
            if (this.currentPage < this.totalPages - 1) { this.currentPage++; this.rebuildWidgets(); }
        }).bounds(x + 195, pageButtonY, 15, 15).build()).active = this.currentPage < this.totalPages - 1;

        int start = this.currentPage * this.bountiesPerPage;
        int end = Math.min(start + this.bountiesPerPage, this.displayableBounties.size());
        
        for (int i = start; i < end; i++) {
            BountyRegistry.Bounty bounty = this.displayableBounties.get(i);
            int displayIndex = i % this.bountiesPerPage;
            
            Button bountyButton;
            int buttonX = x + 162;
            int buttonY = y + 18 + (displayIndex * 24);

            if (ClientReputationData.isBountyActive(bounty.id())) {
                int progress = ClientReputationData.getProgress(bounty.id());
                int required = bounty.requiredAmount();
                
                if (bounty.type() == BountyRegistry.BountyType.KILL) {
                    if (progress >= required) {
                        bountyButton = Button.builder(Component.literal("完成").withStyle(ChatFormatting.GREEN),
                                (button) -> ModMessages.sendToServer(new CompleteBountyC2SPacket(bounty.id())))
                                .bounds(buttonX, buttonY, 50, 20).build();
                    } else {
                        bountyButton = Button.builder(Component.literal(String.format("%d / %d", progress, required)), (b) -> {})
                                .bounds(buttonX, buttonY, 50, 20).build();
                        bountyButton.active = false;
                    }
                } else { // COLLECT
                    bountyButton = Button.builder(Component.literal("回報"),
                            (button) -> ModMessages.sendToServer(new CompleteBountyC2SPacket(bounty.id())))
                            .bounds(buttonX, buttonY, 50, 20).build();
                }
            } else {
                bountyButton = Button.builder(Component.literal("接受"),
                    (button) -> ModMessages.sendToServer(new AcceptBountyC2SPacket(bounty.id())))
                    .bounds(buttonX, buttonY, 50, 20).build();
            }
            this.addRenderableWidget(bountyButton);
        }
    }
    // 【新增】這個公開方法，讓網路封包可以呼叫它來刷新介面
    public void refreshWidgets() {
        this.rebuildWidgets();
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
        renderBountyTooltips(pGuiGraphics, pMouseX, pMouseY);
    }
    
    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        
        int start = this.currentPage * this.bountiesPerPage;
        int end = Math.min(start + this.bountiesPerPage, this.displayableBounties.size());
        
        int bountyY = this.titleLabelY + 12;
        for (int i = start; i < end; i++) {
            BountyRegistry.Bounty bounty = this.displayableBounties.get(i);
            pGuiGraphics.drawString(this.font, Component.literal(bounty.title()).withStyle(bounty.requiredRank().getDisplayName().getStyle()), this.titleLabelX, bountyY, 4210752, false);
            String rewardText = String.format("獎勵: %d 點信譽, %d 元", bounty.repReward(), bounty.moneyReward());
            pGuiGraphics.drawString(this.font, rewardText, this.titleLabelX, bountyY + 9, 4210752, false);
            bountyY += 24;
        }
    }
    
    private void renderBountyTooltips(GuiGraphics guiGraphics, int pMouseX, int pMouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        int start = this.currentPage * this.bountiesPerPage;
        int end = Math.min(start + this.bountiesPerPage, this.displayableBounties.size());
        
        for (int i = start; i < end; i++) {
            int displayIndex = i % this.bountiesPerPage;
            BountyRegistry.Bounty bounty = this.displayableBounties.get(i);
            Component titleComponent = Component.literal(bounty.title()).withStyle(bounty.requiredRank().getDisplayName().getStyle());
            int textWidth = this.font.width(titleComponent);
            int textX = x + this.titleLabelX;
            int textY = y + this.titleLabelY + 12 + (displayIndex * 24);
            int textHeight = 8;

            if (this.isHovering(textX, textY, textWidth, textHeight, pMouseX, pMouseY)) {
                Component description = getBountyDescription(bounty);
                guiGraphics.renderTooltip(this.font, description, pMouseX, pMouseY);
                break;
            }
        }
    }
    
    private Component getBountyDescription(BountyRegistry.Bounty bounty) {
        Component objective;
        if (bounty.target() instanceof EntityType<?> entityType) {
            objective = Component.literal("目標：擊殺 ")
                                 .append(Component.literal(String.valueOf(bounty.requiredAmount())).withStyle(ChatFormatting.YELLOW))
                                 .append(" 隻 ")
                                 .append(Component.translatable(entityType.getDescriptionId()).withStyle(ChatFormatting.AQUA));
        } else if (bounty.target() instanceof Item item) {
            objective = Component.literal("目標：收集 ")
                                 .append(Component.literal(String.valueOf(bounty.requiredAmount())).withStyle(ChatFormatting.YELLOW))
                                 .append(" 個 ")
                                 .append(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.AQUA));
        } else {
            objective = Component.literal("目標：未知");
        }
        return objective;
    }
}