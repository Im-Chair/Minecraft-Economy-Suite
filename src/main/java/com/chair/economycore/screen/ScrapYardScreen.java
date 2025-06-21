package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.network.ModMessages; // 【新增】
import com.chair.economycore.network.packet.SellItemC2SPacket; // 【新增】
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
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        this.addRenderableWidget(Button.builder(Component.literal("出售"), this::onSellButtonPressed)
                .bounds(x + 70, y + 55, 50, 20)
                .build());
    }

    // 【核心修正】修改按鈕行為
    private void onSellButtonPressed(Button button) {
        // 不再使用 menu.clickMenuButton，而是直接發送我們的專屬封包到伺服器
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