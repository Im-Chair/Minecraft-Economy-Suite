package com.chair.economycore.command;

import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.data.WorldProgressionData; // 【新增】
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.ReputationSyncS2CPacket;
import com.chair.economycore.screen.SteleMenu;
import com.chair.economycore.util.Era; // 【新增】
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType; // 【新增】
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel; // 【新增】
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays; //【新增】

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 【新增】/era 主指令
        dispatcher.register(Commands.literal("era")
            .requires(source -> source.hasPermission(2)) // 僅 OP 可用
            // /era get
            .then(Commands.literal("get")
                .executes(ctx -> getEra(ctx.getSource())))
            // /era addcp <amount>
            .then(Commands.literal("addcp")
                .then(Commands.argument("amount", LongArgumentType.longArg(1))
                    .executes(ctx -> addCp(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
            // /era setera <era_name>
            .then(Commands.literal("setera")
                .then(Commands.argument("era_name", StringArgumentType.word())
                    .executes(ctx -> setEra(ctx.getSource(), StringArgumentType.getString(ctx, "era_name")))))
        );
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
    // --- Era 指令的後端邏輯 ---
    private static int getEra(CommandSourceStack source) {
        // 從指令源獲取伺服器世界實例
        ServerLevel level = source.getLevel();
        // 透過我們的方法獲取世界進程數據
        WorldProgressionData progressionData = WorldProgressionData.get(level);
        
        long cp = progressionData.getCivilizationPoints();
        String eraName = progressionData.getCurrentEra().getDisplayName();

        source.sendSuccess(() -> Component.literal("當前時代: " + eraName + " | 文明點數: " + cp), false);
        return 1;
    }

    private static int addCp(CommandSourceStack source, long amount) {
        ServerLevel level = source.getLevel();
        WorldProgressionData progressionData = WorldProgressionData.get(level);
        
        progressionData.addCivilizationPoints(amount);
        source.sendSuccess(() -> Component.literal("已增加 " + amount + " 點文明點數。"), true);
        // 再次查詢以顯示新總數
        return getEra(source);
    }

    private static int setEra(CommandSourceStack source, String eraName) {
        try {
            // 將輸入的字串轉換為我們的 Era Enum
            Era targetEra = Era.valueOf(eraName.toUpperCase());
            ServerLevel level = source.getLevel();
            WorldProgressionData progressionData = WorldProgressionData.get(level);

            progressionData.setCurrentEra(targetEra);
            source.sendSuccess(() -> Component.literal("已將當前時代設定為: " + targetEra.getDisplayName()), true);
            return 1;
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("錯誤：無效的時代名稱。有效名稱為: " + java.util.Arrays.toString(Era.values())));
            return 0;
        }
    }
}