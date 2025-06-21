package com.chair.economycore.network.packet;

import com.chair.economycore.screen.ScrapYardMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// 這是一個從客戶端發送到伺服器的封包
public class SellItemC2SPacket {

    public SellItemC2SPacket() {
        // 這個封包不需要攜帶任何數據，它本身就是一個「出售」的信號
    }

    public SellItemC2SPacket(FriendlyByteBuf buf) {
        // 如果有數據需要讀取，會在這裡進行
    }

    public void toBytes(FriendlyByteBuf buf) {
        // 如果有數據需要寫入，會在這裡進行
    }

    // 伺服器接收到這個封包後，會執行 handle 方法
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 在這裡執行伺服器端的邏輯
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu instanceof ScrapYardMenu menu) {
                // 我們不再依賴 clickMenuButton，而是直接調用一個新的、專門的交易方法
                menu.performSell(player);
            }
        });
        return true;
    }
}