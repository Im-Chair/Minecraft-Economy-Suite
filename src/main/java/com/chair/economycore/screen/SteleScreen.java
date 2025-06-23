package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.client.ClientMoneyData;
import com.chair.economycore.client.ClientReputationData;
import com.chair.economycore.client.ClientWorldProgressionData;
import com.chair.economycore.util.Era;
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
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // 【最終版 - 依使用者偏好調整】
        
        // --- 1. 定義顏色與座標 ---
        int COLOR_TITLE = 0xFF000000; // 黑色標題 (已加入Alpha)
        int COLOR_HEADER = 0xFFFFFF55; // 金黃色主標題 (已加入Alpha)
        int COLOR_TEXT = 0xFFFFFFFF;   // 白色正文 (已加入Alpha)
        int COLOR_ACCENT = 0xFF55FFFF; // 青色點綴 (已加入Alpha)
        int COLOR_MONEY = 0xFF404040;  // 預設金錢顏色(深灰)，使用 0xFF 開頭確保不透明
        
        // 【修正】為分隔線顏色加上 FF 代表不透明
        int COLOR_LINE = 0xFF555555;   // 深灰色分隔線

        int leftColumnX = this.leftPos + 4;
        int rightColumnX = this.leftPos + this.imageWidth / 2 + 4;
        int top = this.topPos + 4;
        int yPos = top;
        // 繪製一條橫跨大部分寬度的水平線
        yPos += 21; // 先下移一點，給標題留出空間
        guiGraphics.fill(this.leftPos + 10, yPos, this.leftPos + this.imageWidth - 10, yPos + 1, COLOR_LINE);
        yPos = top; // 線下的額外間距

        // --- 2. 繪製左欄：個人資訊 ---
        guiGraphics.drawString(this.font, "個人狀態", leftColumnX, yPos, COLOR_TITLE, false);
        guiGraphics.drawString(this.font, "：", leftColumnX+35, yPos, COLOR_TITLE, false);
        //yPos += 10;
        
        // 階級
        Component rankName = ClientReputationData.getPlayerRank().getDisplayName();
        guiGraphics.drawString(this.font, rankName, leftColumnX+40, yPos, COLOR_HEADER, false);
        yPos += 10;
        
        // 信譽
        String repText = "信譽: " + String.format("%,d", ClientReputationData.getPlayerReputation());
        guiGraphics.drawString(this.font, repText, leftColumnX, yPos, COLOR_TEXT, false);
        
        // --- 3. 繪製右欄：世界狀態 ---
        yPos = top; // 重置Y座標
        guiGraphics.drawString(this.font, "世界進程：", rightColumnX, yPos, COLOR_TITLE, false);
        guiGraphics.drawString(this.font, "：", rightColumnX+35, yPos, COLOR_TITLE, false);
        //yPos += 10;

        // 時代
        Component eraName = ClientWorldProgressionData.getCurrentEra().getDisplayName();
        guiGraphics.drawString(this.font, eraName, rightColumnX+40, yPos, COLOR_ACCENT, false);
        yPos += 10;
        
        // CP進度
        Era currentEra = ClientWorldProgressionData.getCurrentEra();
        Era nextEra = ClientWorldProgressionData.getNextEra();
        if(currentEra != nextEra) {
            long currentCp = ClientWorldProgressionData.getCivilizationPoints();
            double progress = (double) currentCp / ClientWorldProgressionData.getRequiredCpForNext() * 100.0;
            String cpText = String.format("%,d (%.1f%%)", currentCp, Math.min(100.0, progress));
            guiGraphics.drawString(this.font, cpText, rightColumnX, yPos, COLOR_TEXT, false);
        } else {
            guiGraphics.drawString(this.font, "已達最終時代", rightColumnX, yPos, COLOR_TEXT, false);
        }

        // --- 4. 繪製精緻的分隔線 ---
        int lineX = this.leftPos + this.imageWidth / 2;
        int lineStartY = this.topPos + 4;
        int lineEndY = lineStartY + 21; // 稍微調整線的高度
        guiGraphics.fill(lineX, lineStartY, lineX + 1, lineEndY, COLOR_LINE);
        
        // --- 5. 【已調整】將餘額繪製在物品欄右上方 ---
        int inventoryTopY = this.topPos + 84; // 物品欄頂部Y座標
        int inventoryRightX = this.leftPos + this.imageWidth - 8; // 物品欄最右側X座標
        String moneyText = "餘額: " + String.format("%,d", ClientMoneyData.getPlayerMoney());
        // 從右往左繪製，對齊右邊緣
        guiGraphics.drawString(this.font, moneyText, inventoryRightX - this.font.width(moneyText), inventoryTopY - 12, COLOR_MONEY, false);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        // 我們已經在 render 方法中繪製了所有文字，這裡留空即可
    }
}