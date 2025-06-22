package com.chair.economycore.network.packet;

import com.chair.economycore.screen.ArtisanMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BuyItemC2SPacket {
    private final int itemIndex;

    public BuyItemC2SPacket(int itemIndex) { this.itemIndex = itemIndex; }
    public BuyItemC2SPacket(FriendlyByteBuf buf) { this.itemIndex = buf.readInt(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeInt(this.itemIndex); }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            // 確保玩家打開的是工匠的介面
            if (player != null && player.containerMenu instanceof ArtisanMenu menu) {
                // 【核心修正】呼叫 Menu 中的購買方法
                menu.performPurchase(player, this.itemIndex);
            }
        });
        return true;
    }
}