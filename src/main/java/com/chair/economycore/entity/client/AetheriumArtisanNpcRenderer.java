package com.chair.economycore.entity.client;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.entity.AetheriumArtisanNpc;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

// 這個渲染器專門用於天穹工匠
public class AetheriumArtisanNpcRenderer extends MobRenderer<AetheriumArtisanNpc, AetheriumArtisanNpcModel> {
    
    // 指向一個新的貼圖路徑
    private static final ResourceLocation TEXTURE = new ResourceLocation(EconomyCore.MODID, "textures/entity/aetherium_artisan_npc.png");

    public AetheriumArtisanNpcRenderer(EntityRendererProvider.Context pContext) {
        // 使用我們為工匠新建的 Model 和 ModelLayer
        super(pContext, new AetheriumArtisanNpcModel(pContext.bakeLayer(ModModelLayers.AETHERIUM_ARTISAN_NPC_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AetheriumArtisanNpc pEntity) {
        return TEXTURE;
    }
}