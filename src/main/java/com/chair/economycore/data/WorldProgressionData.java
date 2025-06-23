package com.chair.economycore.data;

import com.chair.economycore.EconomyCore;
import com.chair.economycore.util.Era;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jetbrains.annotations.NotNull;

public class WorldProgressionData extends SavedData {

    private long civilizationPoints = 0;
    private Era currentEra = Era.WILDERNESS;

    // 這個方法是讀取 NBT 數據並創建實例的核心
    public static WorldProgressionData load(CompoundTag nbt) {
        WorldProgressionData savedData = new WorldProgressionData();
        savedData.civilizationPoints = nbt.getLong("civilization_points");
        try {
            savedData.currentEra = Era.valueOf(nbt.getString("current_era"));
        } catch (IllegalArgumentException e) {
            // 如果讀取失敗，保持預設值
            savedData.currentEra = Era.WILDERNESS;
        }
        return savedData;
    }

    // 這個方法是將實例數據寫入 NBT 的核心
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag nbt) {
        nbt.putLong("civilization_points", this.civilizationPoints);
        nbt.putString("current_era", this.currentEra.name());
        return nbt;
    }

    // --- 從外部獲取我們數據的標準方法 ---
    public static WorldProgressionData get(ServerLevel level) {
        // 獲取主世界的數據儲存庫
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        
        // 嘗試讀取我們的數據，如果不存在，則創建一個新的
        return storage.computeIfAbsent(WorldProgressionData::load, WorldProgressionData::new, EconomyCore.MODID + "_progression_data");
    }

    // --- Getters and Setters ---
    public long getCivilizationPoints() {
        return civilizationPoints;
    }

    public void addCivilizationPoints(long points) {
        this.civilizationPoints += points;
        setDirty(); // 標記為「已變更」，以便在下次儲存世界時寫入硬碟
    }

    public Era getCurrentEra() {
        return currentEra;
    }

    public void setCurrentEra(Era era) {
        this.currentEra = era;
        setDirty();
    }
}