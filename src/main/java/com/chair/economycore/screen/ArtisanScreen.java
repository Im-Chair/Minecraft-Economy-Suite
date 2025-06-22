package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.item.ModItems;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.BuyItemC2SPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ArtisanScreen extends AbstractContainerScreen<ArtisanMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(EconomyCore.MODID, "textures/gui/artisan_gui.png");

    private record ShopItem(ItemStack item, long price, String description) {}
    private final List<ShopItem> shopItems;

    public ArtisanScreen(ArtisanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        // 【核心修正】明確設定 GUI 的尺寸以匹配我們的 222px 高度背景圖
        this.imageHeight = 222; 
        // 根據新的高度，重新計算「物品欄」文字標籤的 y 座標
        this.inventoryLabelY = this.imageHeight - 94;

        this.shopItems = List.of(
                new ShopItem(new ItemStack(ModItems.BOUNDARY_STONE.get()), 500L, "標記一個重要地點。")
        );
    }

    @Override
    protected void init() {
        super.init();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        for (int i = 0; i < this.shopItems.size(); i++) {
            int finalI = i; 
            this.addRenderableWidget(Button.builder(Component.literal("購買"),
                    (button) -> ModMessages.sendToServer(new BuyItemC2SPacket(finalI)))
                    .bounds(x + 110, y + 18 + (i * 22), 50, 20)
                    .build());
        }
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
    
    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);

        for (int i = 0; i < this.shopItems.size(); i++) {
            ShopItem currentItem = this.shopItems.get(i);
            int itemY = 20 + (i * 22);
            
            pGuiGraphics.renderFakeItem(currentItem.item(), 15, itemY - 2);
            pGuiGraphics.drawString(this.font, currentItem.item().getHoverName(), 35, itemY, 4210752, false);
            pGuiGraphics.drawString(this.font, "價格: " + currentItem.price(), 35, itemY + 9, 4210752, false);
        }
    }
}