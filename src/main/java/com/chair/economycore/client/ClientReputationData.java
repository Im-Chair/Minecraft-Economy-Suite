package com.chair.economycore.client;

import com.chair.economycore.util.AdventurerRank;
import java.util.HashMap;
import java.util.Map;

public class ClientReputationData {
    private static long playerReputation;
    private static AdventurerRank playerRank;
    private static Map<String, Integer> bountyProgress = new HashMap<>();

    public static long getPlayerReputation() { return playerReputation; }
    public static AdventurerRank getPlayerRank() { return playerRank == null ? AdventurerRank.F : playerRank; }

    public static boolean isBountyActive(String bountyId) {
        return bountyProgress.containsKey(bountyId);
    }

    public static int getProgress(String bountyId) {
        return bountyProgress.getOrDefault(bountyId, 0);
    }

    public static void set(long reputation, AdventurerRank rank, Map<String, Integer> progress) {
        playerReputation = reputation;
        playerRank = rank;
        bountyProgress = progress;
    }
}