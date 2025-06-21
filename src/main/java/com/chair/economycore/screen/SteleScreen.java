package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.client.ClientMoneyData; // 我們稍後會創建這個
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SteleScreen extends AbstractContainerScreen<SteleMenu> {
  
    // 【核心修正】使用單一字串參數的建構子，將模組ID和路徑用冒號連接起來。
    // 這是更現代且不會被標記為「過時」的標準寫法。
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EconomyCore.MODID + ":" + "textures/gui/stele_gui.png");
    public SteleScreen(SteleMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    // 位於 SteleScreen.java 中
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        // 【重大修正】新版Forge的renderBackground方法不再需要傳入滑鼠和時間參數
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        String moneyText = "餘額: " + ClientMoneyData.getPlayerMoney();
        int textWidth = font.width(moneyText);
        guiGraphics.drawString(this.font, moneyText, this.leftPos + (this.imageWidth - textWidth) / 2, this.topPos + 15, 0x404040, false);
    }
}