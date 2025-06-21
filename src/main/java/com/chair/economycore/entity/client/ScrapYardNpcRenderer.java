package com.chair.economycore.entity.client;

import net.minecraft.client.model.VillagerModel;
// 在 ScrapYardNpcModel.java 中
import net.minecraft.client.model.geom.builders.LayerDefinition; // 【新增】
import net.minecraft.client.model.geom.builders.MeshDefinition; // 【新增】
import com.chair.economycore.EconomyCore;
import com.chair.economycore.entity.ScrapYardNpc;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScrapYardNpcRenderer extends MobRenderer<ScrapYardNpc, ScrapYardNpcModel> {
    
    // 定義我們NPC貼圖的位置，這一步解決了「破圖」問題
    private static final ResourceLocation TEXTURE = new ResourceLocation(EconomyCore.MODID, "textures/entity/scrap_yard_npc.png");

    public ScrapYardNpcRenderer(EntityRendererProvider.Context pContext) {
        // 最後一個參數是實體的陰影大小
        super(pContext, new ScrapYardNpcModel(pContext.bakeLayer(ModModelLayers.SCRAP_YARD_NPC_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ScrapYardNpc pEntity) {
        return TEXTURE;
    }
        // 【新增】這個靜態方法定義了模型的結構，我們直接使用村民的結構
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = VillagerModel.createBodyModel();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}

