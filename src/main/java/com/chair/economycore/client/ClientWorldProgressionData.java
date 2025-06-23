package com.chair.economycore.client;

import com.chair.economycore.util.Era;

/**
 * 【新增檔案】
 * 用於在客戶端儲存從伺服器同步過來的世界進程數據。
 */
public class ClientWorldProgressionData {
    private static Era currentEra = Era.PIONEER;
    private static long civilizationPoints = 0;
    private static Era nextEra = Era.PIONEER;
    private static long requiredCpForNext = 0;

    public static void set(Era currentEra, long civilizationPoints, Era nextEra, long requiredCpForNext) {
        ClientWorldProgressionData.currentEra = currentEra;
        ClientWorldProgressionData.civilizationPoints = civilizationPoints;
        ClientWorldProgressionData.nextEra = nextEra;
        ClientWorldProgressionData.requiredCpForNext = requiredCpForNext;
    }

    public static Era getCurrentEra() {
        return currentEra;
    }

    public static long getCivilizationPoints() {
        return civilizationPoints;
    }

    public static Era getNextEra() {
        return nextEra;
    }

    public static long getRequiredCpForNext() {
        return requiredCpForNext;
    }
}