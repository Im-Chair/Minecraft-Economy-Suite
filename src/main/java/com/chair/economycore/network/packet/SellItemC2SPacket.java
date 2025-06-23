package com.chair.economycore.network.packet;

import com.chair.economycore.screen.ScrapYardMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SellItemC2SPacket {

    public SellItemC2SPacket() {}
    public SellItemC2SPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof ScrapYardMenu menu) {
                // 【核心修正】呼叫 menu 中新的 performSell 方法
                menu.performSell(player);
            }
        });
        return true;
    }
}