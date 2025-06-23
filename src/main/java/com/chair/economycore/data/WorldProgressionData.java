package com.chair.economycore.data;

import com.chair.economycore.util.Era;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * 【已三次修正】
 * - 新增 setCivilizationPoints 和 removeCivilizationPoints 方法
 */
public class WorldProgressionData extends SavedData {

    private static final String DATA_NAME = "ecosuite_world_progression";

    private long civilizationPoints = 0;
    private Era currentEra = Era.PIONEER;

    public static WorldProgressionData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(WorldProgressionData::load, WorldProgressionData::new, DATA_NAME);
    }

    public static WorldProgressionData load(CompoundTag nbt) {
        WorldProgressionData data = new WorldProgressionData();
        data.civilizationPoints = nbt.getLong("CivilizationPoints");
        try {
            data.currentEra = Era.valueOf(nbt.getString("CurrentEra"));
        } catch (IllegalArgumentException e) {
            data.currentEra = Era.PIONEER;
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        nbt.putLong("CivilizationPoints", this.civilizationPoints);
        nbt.putString("CurrentEra", this.currentEra.name());
        return nbt;
    }

    public long getCivilizationPoints() {
        return this.civilizationPoints;
    }

    public void addCivilizationPoints(long amount) {
        if (amount > 0) {
            this.civilizationPoints += amount;
            setDirty();
        }
    }

    /**
     * 【新增】移除指定數量的文明點數
     */
    public void removeCivilizationPoints(long amount) {
        if (amount > 0) {
            this.civilizationPoints = Math.max(0, this.civilizationPoints - amount); // 確保不會變負數
            setDirty();
        }
    }

    /**
     * 【新增】直接設定文明點數為特定值
     */
    public void setCivilizationPoints(long amount) {
        this.civilizationPoints = Math.max(0, amount); // 確保不會變負數
        setDirty();
    }

    public Era getCurrentEra() {
        return this.currentEra;
    }

    public void setCurrentEra(Era newEra) {
        this.currentEra = newEra;
        setDirty();
    }

    public WorldProgressionData() {}
}