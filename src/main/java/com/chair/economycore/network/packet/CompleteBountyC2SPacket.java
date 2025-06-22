package com.chair.economycore.network.packet;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.core.BountyRegistry;
import com.chair.economycore.network.ModMessages;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompleteBountyC2SPacket {
    private final String bountyId;

    public CompleteBountyC2SPacket(String bountyId) { this.bountyId = bountyId; }
    public CompleteBountyC2SPacket(FriendlyByteBuf buf) { this.bountyId = buf.readUtf(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeUtf(this.bountyId); }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            BountyRegistry.Bounty bounty = BountyRegistry.BOUNTIES.get(this.bountyId);
            if (bounty == null) return;

            player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
                if (!rep.isBountyActive(this.bountyId)) return;

                boolean canComplete = false;
                if (bounty.type() == BountyRegistry.BountyType.COLLECT) {
                    Item targetItem = (Item) bounty.target();
                    if (player.getInventory().countItem(targetItem) >= bounty.requiredAmount()) {
                        canComplete = true;
                    }
                } else if (bounty.type() == BountyRegistry.BountyType.KILL) {
                    if (rep.getProgress(this.bountyId) >= bounty.requiredAmount()) {
                        canComplete = true;
                    }
                }

                if (canComplete) {
                    if (bounty.type() == BountyRegistry.BountyType.COLLECT) {
                        int amountToRemove = bounty.requiredAmount();
                        Item targetItem = (Item) bounty.target();
                        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
                            ItemStack currentStack = player.getInventory().getItem(i);
                            if (currentStack.is(targetItem)) {
                                int countInStack = currentStack.getCount();
                                int amountToTake = Math.min(amountToRemove, countInStack);
                                currentStack.shrink(amountToTake);
                                amountToRemove -= amountToTake;
                                if (amountToRemove <= 0) break;
                            }
                        }
                    }

                    rep.completeBounty(this.bountyId);
                    rep.addReputation(bounty.repReward(), player);
                    player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> money.addMoney(bounty.moneyReward()));
                    player.sendSystemMessage(Component.literal("任務完成！" + bounty.title()).withStyle(ChatFormatting.GREEN));
                    
                    syncAllData(player);

                } else {
                    player.sendSystemMessage(Component.literal("任務條件尚未達成！").withStyle(ChatFormatting.RED));
                }
            });
        });
        return true;
    }
    
    private void syncAllData(ServerPlayer player) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player));
        player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> ModMessages.sendToPlayer(new ReputationSyncS2CPacket(rep.getReputation(), rep.getRank(), rep.getBountyProgress()), player));
    }
}