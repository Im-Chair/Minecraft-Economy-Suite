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

    public static final RegistryObject<MenuType<SteleMenu>> STELE_MENU =
            registerMenuType("stele_menu", SteleMenu::new);
            
    public static final RegistryObject<MenuType<ScrapYardMenu>> SCRAP_YARD_MENU =
            registerMenuType("scrap_yard_menu", ScrapYardMenu::new);
            
    public static final RegistryObject<MenuType<ArtisanMenu>> ARTISAN_MENU =
            registerMenuType("artisan_menu", ArtisanMenu::new);

    // 【新增】懸賞板 GUI
    public static final RegistryObject<MenuType<BountyBoardMenu>> BOUNTY_BOARD_MENU =
            registerMenuType("bounty_board_menu", BountyBoardMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}