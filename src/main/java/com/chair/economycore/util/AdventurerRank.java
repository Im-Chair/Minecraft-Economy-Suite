package com.chair.economycore.util;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.Optional;

public enum AdventurerRank {
    // 【核心修正】重新定義每個階級對應的「達成門檻」
    F("F 級冒險者", ChatFormatting.WHITE, 0),
    E("E 級冒險者", ChatFormatting.DARK_GRAY, 1000),
    D("D 級冒險者", ChatFormatting.BLUE, 3000),
    C("C 級冒險者", ChatFormatting.DARK_PURPLE, 8000),
    B("B 級冒險者", ChatFormatting.GOLD, 20000),
    A("A 級冒險者", ChatFormatting.RED, 50000),
    S("S 級冒險者", ChatFormatting.YELLOW, 150000);

    private final Component displayName;
    private final long reputationNeeded;

    AdventurerRank(String name, ChatFormatting color, long reputationNeeded) {
        this.displayName = Component.literal(name).withStyle(color);
        this.reputationNeeded = reputationNeeded;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public long getReputationNeeded() {
        return this.reputationNeeded;
    }
    
    public Optional<AdventurerRank> getNextRank() {
        // S 級是最高級，沒有下一個
        if (this == S) {
            return Optional.empty();
        }
        // 返回列表中的下一個 Enum 常數
        return Optional.of(values()[this.ordinal() + 1]);
    }
}