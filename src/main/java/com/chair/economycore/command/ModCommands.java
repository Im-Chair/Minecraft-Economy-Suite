package com.chair.economycore.command;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.screen.SteleMenu; // GUI 開啟指令仍可保留
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

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        
        // 建立 /money 主指令
        dispatcher.register(Commands.literal("money")
            // 子指令 /money balance [player] - 查詢餘額
            .then(Commands.literal("balance")
                .executes(command -> balance(command.getSource())) // 查詢自己的餘額
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(command -> balance(command.getSource(), EntityArgument.getPlayer(command, "player"))))) // 查詢他人餘額
            
            // 子指令 /money add <player> <amount> - 增加餘額
            .then(Commands.literal("add")
                .requires(source -> source.hasPermission(2)) // 需要OP權限
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", LongArgumentType.longArg(1))
                        .executes(command -> add(command.getSource(), EntityArgument.getPlayer(command, "player"), LongArgumentType.getLong(command, "amount"))))))
            
            // 子指令 /money set <player> <amount> - 設定餘額
            .then(Commands.literal("set")
                .requires(source -> source.hasPermission(2)) // 需要OP權限
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("amount", LongArgumentType.longArg(0))
                        .executes(command -> set(command.getSource(), EntityArgument.getPlayer(command, "player"), LongArgumentType.getLong(command, "amount"))))))
        );

        // 原有的 /opengui 除錯指令可以保留
        dispatcher.register(Commands.literal("opengui")
            .requires(source -> source.hasPermission(2))
            .executes(command -> {
                ServerPlayer player = command.getSource().getPlayerOrException();
                NetworkHooks.openScreen(player, new MenuProvider() {
                    @Override
                    public @NotNull Component getDisplayName() {
                        return Component.literal("古代石碑 (指令測試)");
                    }
                    @Override
                    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
                        return new SteleMenu(pContainerId, pPlayerInventory);
                    }
                });
                return 1;
            }));
    }

    // 查詢餘額的後端邏輯
    private static int balance(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        return balance(source, source.getPlayerOrException());
    }

    private static int balance(CommandSourceStack source, ServerPlayer player) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            source.sendSuccess(() -> Component.literal(player.getName().getString() + " 的餘額是: " + money.getMoney()), false);
        });
        return 1;
    }

    // 增加餘額的後端邏輯
    private static int add(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            money.addMoney(amount);
            source.sendSuccess(() -> Component.literal("已為 " + player.getName().getString() + " 增加了 " + amount + " 元。新餘額: " + money.getMoney()), true);
            
            // 【核心同步邏輯】當餘額變動後，立即發送網路封包給該玩家客戶端
            ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
        });
        return 1;
    }

    // 設定餘額的後端邏輯
    private static int set(CommandSourceStack source, ServerPlayer player, long amount) {
        player.getCapability(PlayerMoneyProvider.PLAYER_MONEY).ifPresent(money -> {
            money.setMoney(amount);
            source.sendSuccess(() -> Component.literal("已將 " + player.getName().getString() + " 的餘額設定為 " + amount), true);
            
            // 【核心同步邏輯】當餘額變動後，立即發送網路封包給該玩家客戶端
            ModMessages.sendToPlayer(new MoneySyncS2CPacket(money.getMoney()), player);
        });
        return 1;
    }
}