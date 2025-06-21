package com.chair.economycore.screen;

import com.chair.economycore.EconomyCore;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, EconomyCore.MODID);

    // 石碑 GUI
    public static final RegistryObject<MenuType<SteleMenu>> STELE_MENU =
            registerMenuType("stele_menu", SteleMenu::new);
            
    // 廢品回收站 GUI
    public static final RegistryObject<MenuType<ScrapYardMenu>> SCRAP_YARD_MENU =
            registerMenuType("scrap_yard_menu", ScrapYardMenu::new);

    // 【修正】確保這個輔助方法存在，解決 registerMenuType 找不到的問題
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}