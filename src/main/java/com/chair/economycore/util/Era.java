package com.chair.economycore.util;

public enum Era {
    WILDERNESS("拓荒時代"),
    FOUNDATION("奠基時代"),
    COMMERCE("商業時代"),
    GOLDEN("黃金時代"),
    STARGAZER("星辰時代");

    private final String displayName;

    Era(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}