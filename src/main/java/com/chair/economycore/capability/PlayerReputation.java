package com.chair.economycore.capability;

import com.chair.economycore.util.AdventurerRank;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerReputation {
    private long reputation;
    private AdventurerRank rank;
    private final Map<String, Integer> bountyProgress;

    public PlayerReputation() {
        this.reputation = 0;
        this.rank = AdventurerRank.F;
        this.bountyProgress = new HashMap<>();
    }

    // --- Getters ---
    public long getReputation() { return this.reputation; }
    public AdventurerRank getRank() { return this.rank; }
    public Map<String, Integer> getBountyProgress() { return this.bountyProgress; }
    public Set<String> getActiveBounties() { return this.bountyProgress.keySet(); }
    public int getProgress(String bountyId) { return this.bountyProgress.getOrDefault(bountyId, 0); }
    
    // --- Setters / Modifiers ---
    public void setRank(AdventurerRank rank) { this.rank = rank; }

    // 這個方法給指令使用，它會檢查階級並通知玩家
    public void setReputation(long reputation, ServerPlayer player) {
        this.reputation = reputation;
        recalculateRank(player);
    }
    
    // 【新增】這個方法專門給 NBT 讀取使用，它不需要玩家物件
    public void setReputationFromNBT(long reputation) {
        this.reputation = reputation;
        recalculateRank(null); // 傳入 null，這樣就不會發送訊息
    }

    public void addReputation(long amount, ServerPlayer player) {
        if (amount <= 0) return;
        this.reputation += amount;
        recalculateRank(player);
    }
    
    private void recalculateRank(ServerPlayer player) {
        AdventurerRank correctRank = AdventurerRank.F;
        for (AdventurerRank rankToCheck : AdventurerRank.values()) {
            if (this.reputation >= rankToCheck.getReputationNeeded()) {
                correctRank = rankToCheck;
            } else {
                break;
            }
        }
        
        if (this.rank != correctRank) {
            this.setRank(correctRank);
            // 【修正】只有在 player 物件存在時才發送訊息
            if (player != null) {
                player.sendSystemMessage(Component.literal("你的冒險者階級已更新為 ").withStyle(ChatFormatting.AQUA)
                                                .append(correctRank.getDisplayName()));
            }
        }
    }
    
    // ... Bounty 相關方法保持不變 ...
    public boolean isBountyActive(String bountyId) { return this.bountyProgress.containsKey(bountyId); }
    public void acceptBounty(String bountyId) { this.bountyProgress.put(bountyId, 0); }
    public void addProgress(String bountyId, int amount) {
        if (isBountyActive(bountyId)) {
            this.bountyProgress.put(bountyId, getProgress(bountyId) + amount);
        }
    }
    public void completeBounty(String bountyId) { this.bountyProgress.remove(bountyId); }
    public void copyFrom(PlayerReputation source) {
        this.reputation = source.getReputation();
        this.rank = source.getRank();
        this.bountyProgress.clear();
        this.bountyProgress.putAll(source.getBountyProgress());
    }
}