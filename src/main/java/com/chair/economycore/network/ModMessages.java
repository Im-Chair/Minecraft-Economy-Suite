package com.chair.economycore.network;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.network.packet.BuyItemC2SPacket;
import com.chair.economycore.network.packet.CompleteBountyC2SPacket;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.ReputationSyncS2CPacket; // 【新增】
import com.chair.economycore.network.packet.SellItemC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import com.chair.economycore.network.packet.AcceptBountyC2SPacket; // 【新增】

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(EconomyCore.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = net;

        // S2C
        net.messageBuilder(MoneySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).decoder(MoneySyncS2CPacket::new).encoder(MoneySyncS2CPacket::toBytes).consumerMainThread(MoneySyncS2CPacket::handle).add();
        // 【新增】註冊信譽同步封包
        net.messageBuilder(ReputationSyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT).decoder(ReputationSyncS2CPacket::new).encoder(ReputationSyncS2CPacket::toBytes).consumerMainThread(ReputationSyncS2CPacket::handle).add();

        // C2S
        net.messageBuilder(SellItemC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).decoder(SellItemC2SPacket::new).encoder(SellItemC2SPacket::toBytes).consumerMainThread(SellItemC2SPacket::handle).add();
        net.messageBuilder(BuyItemC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER).decoder(BuyItemC2SPacket::new).encoder(BuyItemC2SPacket::toBytes).consumerMainThread(BuyItemC2SPacket::handle).add();
        // 【新增】註冊我們的接受任務封包
        net.messageBuilder(AcceptBountyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(AcceptBountyC2SPacket::new)
                .encoder(AcceptBountyC2SPacket::toBytes)
                .consumerMainThread(AcceptBountyC2SPacket::handle)
                .add();
                // 【新增】註冊我們的完成任務封包
        net.messageBuilder(CompleteBountyC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
            .decoder(CompleteBountyC2SPacket::new)
            .encoder(CompleteBountyC2SPacket::toBytes)
            .consumerMainThread(CompleteBountyC2SPacket::handle)
            .add();
            
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}