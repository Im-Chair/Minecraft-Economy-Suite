package com.chair.economycore.network.packet;

import com.chair.economycore.client.ClientMoneyData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class MoneySyncS2CPacket {
    private final long money;

    public MoneySyncS2CPacket(long money) {
        this.money = money;
    }

    public MoneySyncS2CPacket(FriendlyByteBuf buf) {
        this.money = buf.readLong();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(money);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 客戶端：更新本地儲存的金錢數據
            // 【核心修正】將錯誤的方法名稱 set 改為我們定義的 setPlayerMoney
            ClientMoneyData.setPlayerMoney(this.money);
        });
        context.setPacketHandled(true); // 【最佳實踐】明確告訴Forge這個封包已被處理
        return true;
    }
}