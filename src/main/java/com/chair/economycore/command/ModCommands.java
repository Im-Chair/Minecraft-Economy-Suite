package com.chair.economycore.command;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.ReputationSyncS2CPacket;
import com.chair.economycore.screen.SteleMenu;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        
        // --- /money 指令 ---
        dispatcher.register(Commands.literal("money")
            .then(Commands.literal("balance")
                .executes(command -> balance(command.getSource(), command.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(command -> balance(command.getSource(), EntityArgument.getPlayer(command, "player")))))
            
            .then(Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(command -> addMoney(command.getSource(), EntityArgument.getPlayer(command, "player"), LongArgumentType.getLong(command, "amount"))))))
            
            .then(Commands.literal("set")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", LongArgumentType.longArg(0))
                        .executes(command -> setMoney(command.getSource(), EntityArgument.getPlayer(command, "player"), LongArgumentType.getLong(command, "amount"))))))
        );
        
        // --- /rank 指令 ---
        dispatcher.register(Commands.literal("rank")
            .then(Commands.literal("get")
                .executes(ctx -> getRank(ctx.getSource(), ctx.getSource().getPlayerOrException()))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(ctx -> getRank(ctx.getSource(), EntityArgument.getPlayer(ctx, "player")))))
            
            .then(Commands.literal("add")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("rep")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                            .executes(ctx -> addRep(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), LongArgumentType.getLong(ctx, "amount")))))))

            .then(Commands.literal("set")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("rep")
                    .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                            .executes(ctx -> setRep(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), LongArgumentType.getLong(ctx, "amount")))))))
        );

        // --- /opengui 指令 ---
        dispatcher.register(Commands.literal("opengui")
            .requires(source -> source.hasPermission(2))
            .executes(command -> {
                ServerPlayer player = command.getSource().getPlayerOrException();
                NetworkHooks.openScreen(player, new MenuProvider() {
                    @Override public @NotNull Component getDisplayName() { return Component.literal("古代石碑 (指令測試)"); }
                    @Nullable @Override public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
                        return new SteleMenu(pContainerId, pPlayerInventory);
                    }
                });
                return 1;
            }));
    }
    
    // --- Money 指令的後端邏輯 ---
    private static int balance(CommandSourceStack source, ServerPlayer player) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            source.sendSuccess(() -> Component.literal(player.getName().getString() + " 的餘額是: " + money.getMoney()), false);
        });
        return 1;
    }

    private static int addMoney(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            money.addMoney(amount);
            source.sendSuccess(() -> Component.literal("已為 " + player.getName().getString() + " 增加了 " + amount + " 元。新餘額: " + money.getMoney()), true);
            ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
        });
        return 1;
    }

    private static int setMoney(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            money.setMoney(amount);
            source.sendSuccess(() -> Component.literal("已將 " + player.getName().getString() + " 的餘額設定為 " + amount), true);
            ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
        });
        return 1;
    }

    // --- Rank 指令的後端邏輯 ---
    private static int getRank(CommandSourceStack source, ServerPlayer player) {
        player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
            source.sendSuccess(() -> Component.literal(player.getName().getString() + " 的階級是: ")
                    .append(rep.getRank().getDisplayName())
                    .append(Component.literal("，信譽值: " + rep.getReputation())), false);
        });
        return 1;
    }
    
    private static int addRep(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
            rep.addReputation(amount, player);
            source.sendSuccess(() -> Component.literal("已為 " + player.getName().getString() + " 增加了 " + amount + " 點信譽。新信譽值: " + rep.getReputation()), true);
            ModMessages.sendToPlayer(new ReputationSyncS2CPacket(rep.getReputation(), rep.getRank(), rep.getBountyProgress()), player);
        });
        return 1;
    }
    
    private static int setRep(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerReputationProvider.PLAYER_REPUTATION).ifPresent(rep -> {
            rep.setReputation(amount, player);
            source.sendSuccess(() -> Component.literal("已將 " + player.getName().getString() + " 的信譽值設定為 " + amount), true);
            ModMessages.sendToPlayer(new ReputationSyncS2CPacket(rep.getReputation(), rep.getRank(), rep.getBountyProgress()), player);
        });
        return 1;
    }
}