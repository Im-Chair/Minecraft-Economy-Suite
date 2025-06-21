package com.chair.economycore.entity.client;

import com.chair.economycore.entity.ScrapYardNpc;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;

public class ScrapYardNpcModel extends VillagerModel<ScrapYardNpc> {

    public ScrapYardNpcModel(ModelPart pRoot) {
        super(pRoot);
    }

    // 【核心修正】將這個方法設定為 public，允許其他套件的類別存取它
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = VillagerModel.createBodyModel();
        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}