package com.chair.economycore.util;

import net.minecraft.network.chat.Component;

/**
 * 【已修改】
 * 世界時代的列舉。
 * 直接在您原有的 Enum 基礎上進行了功能擴充。
 * 增加了晉升所需CP、多國語言的翻譯鍵、以及獲取下個時代的便捷方法。
 */
public enum Era {
    // 在這裡定義您的時代
    // 格式：(晉升所需CP, 多國語言翻譯鍵)
    PIONEER(0, "era.economysuite.pioneer"),          // 拓荒時代
    SETTLEMENT(10000, "era.economysuite.settlement"), // 開墾時代
    COMMERCE(50000, "era.economysuite.commerce"),     // 商業時代
    INDUSTRY(250000, "era.economysuite.industry"),    // 工業時代
    AETHER(1000000, "era.economysuite.aether"),      // 天穹時代
    STAR(5000000, "era.economysuite.star");          // 星辰時代

    // --- 以下是新增的程式碼 ---
    private final long requiredCp;
    private final String translationKey;

    Era(long requiredCp, String translationKey) {
        this.requiredCp = requiredCp;
        this.translationKey = translationKey;
    }

    /**
     * 獲取晉升到這個時代所需的總CP點數。
     * @return 所需的CP點數
     */
    public long getRequiredCp() {
        return this.requiredCp;
    }

    /**
     * 獲取本地化的時代顯示名稱。
     * @return 代表時代名稱的Component
     */
    public Component getDisplayName() {
        return Component.translatable(this.translationKey);
    }

    /**
     * 獲取下一個時代。如果已是最終時代，則返回自身。
     * @return Era 下一個時代
     */
    public Era getNext() {
        if (this.ordinal() < values().length - 1) {
            return values()[this.ordinal() + 1];
        }
        return this; // 如果是最後一個時代，就返回自己
    }
}