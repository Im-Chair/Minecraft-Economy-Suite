package com.chair.economycore.block;

import com.chair.economycore.EconomyCore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EconomyCore.MODID);

    // 註冊我們的懸賞板方塊
    public static final RegistryObject<Block> BOUNTY_BOARD = BLOCKS.register("bounty_board",
            () -> new BountyBoardBlock(BlockBehaviour.Properties.of()
                    .strength(2.5F).sound(SoundType.WOOD))); // 設定硬度和聲音，使其像木頭

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}