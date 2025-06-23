package com.chair.economycore.network.packet;

import com.chair.economycore.client.ClientWorldProgressionData;
import com.chair.economycore.util.Era;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 【新增檔案】
 * 從伺服器到客戶端(S2C)同步世界進程數據的封包。
 */
public class WorldProgressionSyncS2CPacket {
    private final Era currentEra;
    private final long civilizationPoints;
    private final Era nextEra;
    private final long requiredCpForNext;

    // 在伺服器端建立封包時使用
    public WorldProgressionSyncS2CPacket(Era currentEra, long civilizationPoints, Era nextEra, long requiredCpForNext) {
        this.currentEra = currentEra;
        this.civilizationPoints = civilizationPoints;
        this.nextEra = nextEra;
        this.requiredCpForNext = requiredCpForNext;
    }

    // 從網路數據中讀取並建立封包時使用
    public WorldProgressionSyncS2CPacket(FriendlyByteBuf buf) {
        this.currentEra = buf.readEnum(Era.class);
        this.civilizationPoints = buf.readLong();
        this.nextEra = buf.readEnum(Era.class);
        this.requiredCpForNext = buf.readLong();
    }

    // 將封包寫入網路數據
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.currentEra);
        buf.writeLong(this.civilizationPoints);
        buf.writeEnum(this.nextEra);
        buf.writeLong(this.requiredCpForNext);
    }

    // 客戶端收到封包後的處理邏輯
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 確保在客戶端執行緒上操作
            // 將收到的數據設置到我們的客戶端儲存區
            ClientWorldProgressionData.set(currentEra, civilizationPoints, nextEra, requiredCpForNext);
        });
        return true;
    }
}