package com.chair.economycore.event;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.command.ModCommands;
import com.chair.economycore.core.BountyRegistry;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.ReputationSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EconomyCore.MODID)
public class ForgeEvents {

    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerMoneyProvider.PLAYER_MONEY).isPresent()) {
                event.addCapability(new ResourceLocation(EconomyCore.MODID, "money"), new PlayerMoneyProvider());
            }
            if (!event.getObject().getCapability(PlayerReputationProvider.PLAYER_REPUTATION).isPresent()) {
                event.addCapability(new ResourceLocation(EconomyCore.MODID, "reputation"), new PlayerReputationProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(oldStore -> event.getEntity().getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(newStore -> newStore.copyFrom(oldStore)));
            event.getOriginal().getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(oldStore -> event.getEntity().getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(newStore -> newStore.copyFrom(oldStore)));
        }
    }
    
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAllData(player);
        }
    }
    
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAllData(player);
        }
    }
    
    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncAllData(player);
        }
    }
    
    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
                for (String bountyId : rep.getActiveBounties()) {
                    BountyRegistry.Bounty bounty = BountyRegistry.BOUNTIES.get(bountyId);
                    if (bounty != null && bounty.type() == BountyRegistry.BountyType.KILL && bounty.target() == event.getEntity().getType()) {
                        if (rep.getProgress(bountyId) < bounty.requiredAmount()) {
                            rep.addProgress(bountyId, 1);
                            syncAllData(player);
                        }
                    }
                }
            });
        }
    }

    private static void syncAllData(ServerPlayer player) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player));
        player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> ModMessages.sendToPlayer(new ReputationSyncS2CPacket(rep.getReputation(), rep.getRank(), rep.getBountyProgress()), player));
    }
}