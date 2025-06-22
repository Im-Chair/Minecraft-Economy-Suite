package com.chair.economycore.entity.client;

import com.chair.economycore.entity.AetheriumArtisanNpc;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

// 這個模型類別專門用於天穹工匠
public class AetheriumArtisanNpcModel extends VillagerModel<AetheriumArtisanNpc> {

    public AetheriumArtisanNpcModel(ModelPart pRoot) {
        super(pRoot);
    }

    // 我們依然借用村民的身體結構
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = VillagerModel.createBodyModel();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}