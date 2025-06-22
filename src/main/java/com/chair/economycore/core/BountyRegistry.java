package com.chair.economycore.core;

import com.chair.economycore.util.AdventurerRank;
import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.Map;

public class BountyRegistry {

    public enum BountyType { KILL, COLLECT }

    // 【核心修正】Bounty record 中新增 String id
    public record Bounty(
            String id, String title, BountyType type, Object target, int requiredAmount,
            long repReward, long moneyReward, AdventurerRank requiredRank
    ) {}

    // 【核心修正】將 BOUNTIES 從 List 改為 Map<String, Bounty>
    public static final Map<String, Bounty> BOUNTIES;

    static {
        // 使用 Builder 來創建一個不可變的 Map
        ImmutableMap.Builder<String, Bounty> builder = ImmutableMap.builder();
        
        builder.put("kill_10_slimes", new Bounty("kill_10_slimes", "【F級】清理史萊姆", BountyType.KILL, EntityType.SLIME, 10, 50L, 100L, AdventurerRank.F));
        builder.put("collect_32_leather", new Bounty("collect_32_leather", "【F級】皮革供應商", BountyType.COLLECT, Items.LEATHER, 32, 40L, 120L, AdventurerRank.F));
        builder.put("kill_20_zombies", new Bounty("kill_20_zombies", "【D級】礦洞探險家", BountyType.KILL, EntityType.ZOMBIE, 20, 150L, 300L, AdventurerRank.D));
        builder.put("kill_15_creepers", new Bounty("kill_15_creepers", "【C級】消除威脅", BountyType.KILL, EntityType.CREEPER, 15, 200L, 500L, AdventurerRank.C));
        builder.put("collect_5_blaze_rods", new Bounty("collect_5_blaze_rods", "【B級】烈焰的證明", BountyType.COLLECT, Items.BLAZE_ROD, 5, 500L, 2000L, AdventurerRank.B));
        builder.put("kill_1_ender_dragon", new Bounty("kill_1_ender_dragon", "【A級】獵龍委託", BountyType.KILL, EntityType.ENDER_DRAGON, 1, 10000L, 50000L, AdventurerRank.A));
        
        BOUNTIES = builder.build();
    }
}