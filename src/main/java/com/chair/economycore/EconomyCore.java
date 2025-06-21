package com.chair.economycore;

import com.chair.economycore.entity.ModEntities;
import com.chair.economycore.entity.client.ModModelLayers;
import com.chair.economycore.entity.client.ScrapYardNpcModel;
import com.chair.economycore.entity.client.ScrapYardNpcRenderer;
import com.chair.economycore.item.ModItems;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.screen.ModMenuTypes;
import com.chair.economycore.screen.ScrapYardScreen;
import com.chair.economycore.screen.SteleScreen;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(EconomyCore.MODID)
public class EconomyCore {
    public static final String MODID = "economycore";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EconomyCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModEntities.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onRegisterLayers);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // 原有的石碑 GUI 註冊
            MenuScreens.register(ModMenuTypes.STELE_MENU.get(), SteleScreen::new);
            // 【新增】廢品回收站 GUI 註冊
            MenuScreens.register(ModMenuTypes.SCRAP_YARD_MENU.get(), ScrapYardScreen::new);
            
            // 實體渲染器註冊
            EntityRenderers.register(ModEntities.SCRAP_YARD_NPC.get(), ScrapYardNpcRenderer::new);
        });
    }

    public void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.SCRAP_YARD_NPC_LAYER, ScrapYardNpcModel::createBodyLayer);
    }
}