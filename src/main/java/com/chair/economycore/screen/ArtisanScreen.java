package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.core.ArtisanShopRegistry;
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

    private static final ResourceLocation TEXTURE = new ResourceLocation(EconomyCore.MODID, "textures/gui/artisan_gui.png");

    private final List<ArtisanShopRegistry.ShopItem> shopItems;

    public ArtisanScreen(ArtisanMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 220;
        this.imageHeight = 222;
        this.inventoryLabelY = this.imageHeight - 94;
        this.shopItems = ArtisanShopRegistry.SHOP_ITEMS;
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
                    .bounds(x + 132, y + 18 + (i * 22), 30, 20)
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
        
        int playerInvX = (this.imageWidth - 162) / 2;
        //pGuiGraphics.drawString(this.font, this.playerInventoryTitle, playerInvX, this.inventoryLabelY, 4210752, false);

        int itemY = this.titleLabelY + 12;
        for (ArtisanShopRegistry.ShopItem shopItem : this.shopItems) {
            pGuiGraphics.renderFakeItem(shopItem.item(), 15, itemY - 2);
            pGuiGraphics.drawString(this.font, shopItem.item().getHoverName(), 35, itemY, 4210752, false);
            pGuiGraphics.drawString(this.font, "價格: " + shopItem.price(), 35, itemY + 9, 4210752, false);
            itemY += 22;
        }
    }
}