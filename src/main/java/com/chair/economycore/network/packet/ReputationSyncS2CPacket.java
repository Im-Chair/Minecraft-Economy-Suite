package com.chair.economycore.network.packet;

import com.chair.economycore.client.ClientReputationData;
import com.chair.economycore.screen.BountyBoardScreen; // 【新增】
import com.chair.economycore.util.AdventurerRank;
import net.minecraft.client.Minecraft; // 【新增】
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ReputationSyncS2CPacket {
    private final long reputation;
    private final AdventurerRank rank;
    private final Map<String, Integer> bountyProgress;

    public ReputationSyncS2CPacket(long reputation, AdventurerRank rank, Map<String, Integer> bountyProgress) {
        this.reputation = reputation;
        this.rank = rank;
        this.bountyProgress = bountyProgress;
    }

    public ReputationSyncS2CPacket(FriendlyByteBuf buf) {
        this.reputation = buf.readLong();
        this.rank = buf.readEnum(AdventurerRank.class);
        this.bountyProgress = buf.readMap(HashMap::new, FriendlyByteBuf::readUtf, FriendlyByteBuf::readInt);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeLong(reputation);
        buf.writeEnum(rank);
        buf.writeMap(this.bountyProgress, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeInt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 1. 更新客戶端的數據儲存
            ClientReputationData.set(this.reputation, this.rank, this.bountyProgress);
            
            // 【核心修正】2. 檢查當前打開的畫面是否為懸賞板，如果是，則命令它刷新
            if (Minecraft.getInstance().screen instanceof BountyBoardScreen screen) {
                screen.refreshWidgets();
            }
        });
        return true;
    }
}