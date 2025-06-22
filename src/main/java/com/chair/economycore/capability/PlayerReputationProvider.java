package com.chair.economycore.capability;

import com.chair.economycore.util.AdventurerRank;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerReputationProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<PlayerReputation> PLAYER_REPUTATION =
            CapabilityManager.get(new CapabilityToken<>() {});

    private PlayerReputation reputation = null;
    private final LazyOptional<PlayerReputation> optional = LazyOptional.of(this::createPlayerReputation);

    private PlayerReputation createPlayerReputation() {
        if (this.reputation == null) {
            this.reputation = new PlayerReputation();
        }
        return this.reputation;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_REPUTATION) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        PlayerReputation rep = createPlayerReputation();
        nbt.putLong("reputation", rep.getReputation());
        nbt.putString("rank", rep.getRank().name());
        
        ListTag progressList = new ListTag();
        rep.getBountyProgress().forEach((id, progress) -> {
            CompoundTag entry = new CompoundTag();
            entry.putString("Id", id);
            entry.putInt("Progress", progress);
            progressList.add(entry);
        });
        nbt.put("bounty_progress", progressList);
        
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        PlayerReputation rep = createPlayerReputation();
        
        // 【核心修正】呼叫專門為 NBT 創建的新方法
        rep.setReputationFromNBT(nbt.getLong("reputation"));
        
        // rank 的讀取被 recalculateRank() 內部處理了，但為了保險起見，我們保留這個
        if (nbt.contains("rank", Tag.TAG_STRING)) {
            try {
                // 如果存檔中的階級比計算出的階級高，保留存檔中的（例如管理員手動設定）
                AdventurerRank savedRank = AdventurerRank.valueOf(nbt.getString("rank"));
                if (savedRank.ordinal() > rep.getRank().ordinal()) {
                    rep.setRank(savedRank);
                }
            } catch (IllegalArgumentException e) {
                // do nothing, keep calculated rank
            }
        }
        
        rep.getBountyProgress().clear();
        if (nbt.contains("bounty_progress", Tag.TAG_LIST)) {
            ListTag progressList = nbt.getList("bounty_progress", Tag.TAG_COMPOUND);
            for (Tag tag : progressList) {
                CompoundTag entry = (CompoundTag) tag;
                rep.getBountyProgress().put(entry.getString("Id"), entry.getInt("Progress"));
            }
        }
    }
}