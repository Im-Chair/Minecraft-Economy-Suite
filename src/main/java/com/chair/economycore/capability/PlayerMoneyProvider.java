package com.chair.economycore.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerMoneyProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    
    public static final Capability<PlayerMoney> PLAYER_MONEY = CapabilityManager.get(new CapabilityToken<>() { });

    private PlayerMoney money = null;
    private final LazyOptional<PlayerMoney> optional = LazyOptional.of(this::createPlayerMoney);

    private PlayerMoney createPlayerMoney() {
        if (this.money == null) {
            this.money = new PlayerMoney();
        }
        return this.money;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == PLAYER_MONEY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    // 將能力數據序列化 (保存到磁碟)
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        // 【核心修正】直接使用 createPlayerMoney() 獲取實例，不再需要 ifPresent
        nbt.putLong("money", createPlayerMoney().getMoney());
        return nbt;
    }

    // 從磁碟讀取數據，反序列化到能力中
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // 【核心修正】直接使用 createPlayerMoney() 獲取實例，不再需要 ifPresent
        createPlayerMoney().setMoney(nbt.getLong("money"));
    }
}