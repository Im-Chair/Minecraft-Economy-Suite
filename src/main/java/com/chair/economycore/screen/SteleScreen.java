package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.client.ClientMoneyData;
import com.chair.economycore.client.ClientReputationData; // 【新增】引入信譽數據
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class SteleScreen extends AbstractContainerScreen<SteleMenu> {
    
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EconomyCore.MODID, "textures/gui/stele_gui.png");

    public SteleScreen(SteleMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
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
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // 【核心修正】在這裡繪製所有我們需要的資訊
        int centerX = this.leftPos + this.imageWidth / 2;
        
        // 1. 繪製餘額
        String moneyText = "餘額: " + ClientMoneyData.getPlayerMoney();
        int moneyTextWidth = this.font.width(moneyText);
        guiGraphics.drawString(this.font, moneyText, centerX - moneyTextWidth / 2, this.topPos + 20, 4210752, false);
        
        // 2. 繪製階級
        Component rankText = ClientReputationData.getPlayerRank().getDisplayName();
        int rankTextWidth = this.font.width(rankText);
        guiGraphics.drawString(this.font, rankText, centerX - rankTextWidth / 2, this.topPos + 40, 4210752, false);
        
        // 3. 繪製信譽值
        String repText = "信譽值: " + ClientReputationData.getPlayerReputation();
        int repTextWidth = this.font.width(repText);
        guiGraphics.drawString(this.font, repText, centerX - repTextWidth / 2, this.topPos + 50, 4210752, false);
    }
    
    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // 我們已經在 render 方法中繪製了所有文字，這裡留空即可
    }
}