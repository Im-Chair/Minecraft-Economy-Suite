package com.chair.economycore.entity.client;

import com.chair.economycore.EconomyCore;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {
    public static final ModelLayerLocation SCRAP_YARD_NPC_LAYER = new ModelLayerLocation(
            new ResourceLocation(EconomyCore.MODID, "scrap_yard_npc_layer"), "main");
            
    // 【新增】為天穹工匠定義一個新的模型圖層
    public static final ModelLayerLocation AETHERIUM_ARTISAN_NPC_LAYER = new ModelLayerLocation(
            new ResourceLocation(EconomyCore.MODID, "aetherium_artisan_npc_layer"), "main");
}