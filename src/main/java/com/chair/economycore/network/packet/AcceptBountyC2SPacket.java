package com.chair.economycore.network.packet;

import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.core.BountyRegistry;
import com.chair.economycore.network.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class AcceptBountyC2SPacket {
    private final String bountyId;

    public AcceptBountyC2SPacket(String bountyId) { this.bountyId = bountyId; }
    public AcceptBountyC2SPacket(FriendlyByteBuf buf) { this.bountyId = buf.readUtf(); }
    public void toBytes(FriendlyByteBuf buf) { buf.writeUtf(this.bountyId); }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            
            BountyRegistry.Bounty selectedBounty = BountyRegistry.BOUNTIES.get(this.bountyId);
            if (selectedBounty != null) {
                player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
                    if (rep.isBountyActive(this.bountyId)) {
                        player.sendSystemMessage(Component.literal("你已經在執行這個任務了！"));
                    } else {
                        rep.acceptBounty(this.bountyId);
                        player.sendSystemMessage(Component.literal("你接受了任務：" + selectedBounty.title()));
                        ModMessages.sendToPlayer(new ReputationSyncS2CPacket(rep.getReputation(), rep.getRank(), rep.getBountyProgress()), player);
                    }
                });
            }
        });
        return true;
    }
}