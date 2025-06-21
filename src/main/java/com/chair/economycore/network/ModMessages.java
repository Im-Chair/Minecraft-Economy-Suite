package com.chair.economycore.network;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.SellItemC2SPacket; // 【新增】引入新封包
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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

        // 原有的 S2C 金錢同步封包
        net.messageBuilder(MoneySyncS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(MoneySyncS2CPacket::new)
                .encoder(MoneySyncS2CPacket::toBytes)
                .consumerMainThread(MoneySyncS2CPacket::handle)
                .add();
                
        // 【新增】註冊我們的 C2S 出售物品封包
        net.messageBuilder(SellItemC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(SellItemC2SPacket::new)
                .encoder(SellItemC2SPacket::toBytes)
                .consumerMainThread(SellItemC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    // 【新增】一個從客戶端發送訊息到伺服器的方法
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}