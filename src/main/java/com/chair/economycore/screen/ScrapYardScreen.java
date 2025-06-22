package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.SellItemC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ScrapYardScreen extends AbstractContainerScreen<ScrapYardMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EconomyCore.MODID, "textures/gui/scrap_yard_gui.png");

    public ScrapYardScreen(ScrapYardMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        // 【修正】確保使用標準的 166 高度
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        
        // 【核心修正】重新計算按鈕座標，使其完美置中
        // 物品欄中心點 x = 80 + 9 = 89
        // 按鈕寬度 50，所以按鈕起始點 x = 89 - (50/2) = 64
        this.addRenderableWidget(Button.builder(Component.literal("出售"), this::onSellButtonPressed)
                .bounds(x + 63, y + 58, 50, 20)
                .build());
    }
    
    private void onSellButtonPressed(Button button) {
        ModMessages.sendToServer(new SellItemC2SPacket());
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
    }
}