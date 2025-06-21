package com.chair.economycore.event;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.capability.PlayerMoney;
import com.chair.economycore.entity.ModEntities;
import com.chair.economycore.entity.ScrapYardNpc;
import com.chair.economycore.item.ModItems;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EconomyCore.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {

    @SubscribeEvent
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerMoney.class);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SCRAP_YARD_NPC.get(),
                ScrapYardNpc.createAttributes().build());
    }
    
    // 【搬回來的，正確的】負責將物品添加到創造模式物品欄
    @SubscribeEvent
    public static void onBuildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        // 將古代石碑添加到「原料」分頁
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.ANCIENT_STELE);
        }

        // 【採納你的建議】為了方便尋找，暫時將生成蛋添加到「戰鬥用品」分頁
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.SCRAP_YARD_NPC_SPAWN_EGG);
        }
    }
}