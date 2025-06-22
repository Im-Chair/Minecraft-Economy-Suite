package com.chair.economycore.event;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.capability.PlayerMoney;
import com.chair.economycore.capability.PlayerReputation; // 【新增】引入新的能力類別
import com.chair.economycore.entity.AetheriumArtisanNpc;
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
        // 在這裡註冊我們所有的能力
        event.register(PlayerMoney.class);
        // 【新增】註冊玩家信譽能力
        event.register(PlayerReputation.class);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.SCRAP_YARD_NPC.get(),
                ScrapYardNpc.createAttributes().build());
        event.put(ModEntities.AETHERIUM_ARTISAN_NPC.get(),
                AetheriumArtisanNpc.createAttributes().build());
    }
    
    @SubscribeEvent
    public static void onBuildCreativeModeTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.ANCIENT_STELE);
            event.accept(ModItems.BOUNDARY_STONE);
        }
        
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.SCRAP_YARD_NPC_SPAWN_EGG);
            event.accept(ModItems.AETHERIUM_ARTISAN_NPC_SPAWN_EGG);
        }
        
        // 【新增】將懸賞板添加到「功能性方塊」分頁
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.BOUNTY_BOARD_ITEM);
        }
    }
}