package com.chair.economycore.event;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EconomyCore.MODID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerMoneyProvider.PLAYER_MONEY).isPresent()) {
                event.addCapability(new ResourceLocation(EconomyCore.MODID, "money"), new PlayerMoneyProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMoney(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMoney(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncMoney(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    private static void syncMoney(ServerPlayer player) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
        });
    }
}