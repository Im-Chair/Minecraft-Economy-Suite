package com.chair.economycore.core;

import com.chair.economycore.data.WorldProgressionData;
import com.chair.economycore.util.Era;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

/**
 * 【已三次修正】
 * - 將 checkAgeProgression 和 getScaledRequiredCp 的權限從 private 改為 public
 */
public class AgeManager {

    public static void addCivilizationPoints(MinecraftServer server, long amount, ServerPlayer sourcePlayer) {
        if (amount <= 0 || server == null) return;

        ServerLevel overworld = server.overworld();
        WorldProgressionData progressData = WorldProgressionData.get(overworld);

        progressData.addCivilizationPoints(amount);

        checkAgeProgression(server);
    }

    /**
     * 【修正】權限改為 public，以便 ModCommands 可以呼叫。
     * 檢查世界是否滿足晉升到下一個時代的條件。
     */
    public static void checkAgeProgression(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        WorldProgressionData progressData = WorldProgressionData.get(overworld);
        Era currentEra = progressData.getCurrentEra();
        Era nextEra = currentEra.getNext();

        if (currentEra == nextEra) return; 

        long currentCp = progressData.getCivilizationPoints();
        long requiredCp = getScaledRequiredCp(server, nextEra);

        if (currentCp >= requiredCp) {
            advanceToNextAge(server, nextEra);
        }
    }

    private static void advanceToNextAge(MinecraftServer server, Era newEra) {
        ServerLevel overworld = server.overworld();
        WorldProgressionData progressData = WorldProgressionData.get(overworld);
        progressData.setCurrentEra(newEra);

        Component message = Component.translatable("chat.economysuite.age_advance", newEra.getDisplayName())
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        server.getPlayerList().broadcastSystemMessage(message, false);

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.playNotifySound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1.0f, 1.0f);
        }

        // TODO: 在此處觸發天空之城建築生成
    }

    /**
     * 【修正】權限改為 public，以便 ModCommands 可以呼叫。
     * 根據伺服器上的玩家數量，動態計算晉升所需的CP。
     */
    public static long getScaledRequiredCp(MinecraftServer server, Era targetEra) {
        long baseCp = targetEra.getRequiredCp();
        int playerCount = server.getPlayerCount();
        double scalingFactor = 0.05 * (playerCount - 1);
        if (scalingFactor < 0) scalingFactor = 0;
        return (long)(baseCp * (1 + scalingFactor));
    }
}