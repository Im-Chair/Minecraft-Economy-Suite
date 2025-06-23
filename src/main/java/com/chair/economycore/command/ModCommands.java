package com.chair.economycore.command;

import com.chair.economycore.core.AgeManager;
import com.chair.economycore.data.WorldProgressionData;
import com.chair.economycore.util.Era;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

// ... (您其他的 import 保持不變)
import com.chair.economycore.capability.PlayerMoneyProvider;
import com.chair.economycore.capability.PlayerReputationProvider;
import com.chair.economycore.network.ModMessages;
import com.chair.economycore.network.packet.MoneySyncS2CPacket;
import com.chair.economycore.network.packet.ReputationSyncS2CPacket;
import com.chair.economycore.screen.SteleMenu;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Arrays;


public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 【/era 指令重構】新增 setcp 和 removecp，並修正 get
        dispatcher.register(Commands.literal("era")
                .requires(source -> source.hasPermission(2))
                // /era get
                .then(Commands.literal("get")
                        .executes(ctx -> getEra(ctx.getSource())))
                // /era addcp <amount>
                .then(Commands.literal("addcp")
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                .executes(ctx -> addCp(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                // 【新增】/era removecp <amount>
                .then(Commands.literal("removecp")
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                                .executes(ctx -> removeCp(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                // 【新增】/era setcp <amount>
                .then(Commands.literal("setcp")
                        .then(Commands.argument("amount", LongArgumentType.longArg(0))
                                .executes(ctx -> setCp(ctx.getSource(), LongArgumentType.getLong(ctx, "amount")))))
                // /era setera <era_name>
                .then(Commands.literal("setera")
                        .then(Commands.argument("era_name", StringArgumentType.word())
                                .executes(ctx -> setEra(ctx.getSource(), StringArgumentType.getString(ctx, "era_name")))))
        );
        
        // ... (您其他的指令 /money, /rank 等保持不變)
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

    // --- Era 指令的後端邏輯 (已完全重構) ---

    private static int getEra(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        WorldProgressionData data = WorldProgressionData.get(level);
        Era currentEra = data.getCurrentEra();
        long currentCp = data.getCivilizationPoints();

        // 建立訊息元件
        source.sendSuccess(() -> Component.literal("======= 世界進程報告 =======").withStyle(ChatFormatting.GOLD), false);
        source.sendSuccess(() -> Component.literal("當前時代: ").withStyle(ChatFormatting.AQUA)
                .append(currentEra.getDisplayName().copy().withStyle(ChatFormatting.WHITE)), false);
        source.sendSuccess(() -> Component.literal("文明點數: ").withStyle(ChatFormatting.AQUA)
                .append(Component.literal(String.format("%,d", currentCp)).withStyle(ChatFormatting.WHITE)), false);

        // 如果不是最終時代，則顯示下一時代的進度
        Era nextEra = currentEra.getNext();
        if (currentEra != nextEra) {
            long requiredCp = AgeManager.getScaledRequiredCp(source.getServer(), nextEra);
            double progress = Math.min(100.0, (double) currentCp / requiredCp * 100.0);

            source.sendSuccess(() -> Component.literal("下一時代: ").withStyle(ChatFormatting.AQUA)
                    .append(nextEra.getDisplayName().copy().withStyle(ChatFormatting.WHITE)), false);
            source.sendSuccess(() -> Component.literal("晉升需求: ").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(String.format("%,d", requiredCp)).withStyle(ChatFormatting.WHITE)), false);
            source.sendSuccess(() -> Component.literal("進度: ").withStyle(ChatFormatting.AQUA)
                    .append(Component.literal(String.format("%.2f%%", progress)).withStyle(ChatFormatting.WHITE)), false);
        } else {
            source.sendSuccess(() -> Component.literal("世界已達到最終的黃金時代！").withStyle(ChatFormatting.GOLD), false);
        }
        source.sendSuccess(() -> Component.literal("=========================").withStyle(ChatFormatting.GOLD), false);

        return 1;
    }

    private static int addCp(CommandSourceStack source, long amount) {
        // 呼叫 AgeManager 來處理，它會自動檢查晉升
        AgeManager.addCivilizationPoints(source.getServer(), amount, source.getPlayer());
        
        // 取得更新後的總數
        long newTotal = WorldProgressionData.get(source.getLevel()).getCivilizationPoints();
        source.sendSuccess(() -> Component.literal("成功增加 " + String.format("%,d", amount) + " 點CP。")
                .append("目前總數: " + String.format("%,d", newTotal)), true);
        return 1;
    }

    private static int removeCp(CommandSourceStack source, long amount) {
        WorldProgressionData data = WorldProgressionData.get(source.getLevel());
        data.removeCivilizationPoints(amount);
        
        long newTotal = data.getCivilizationPoints();
        source.sendSuccess(() -> Component.literal("成功移除 " + String.format("%,d", amount) + " 點CP。")
                .append("目前總數: " + String.format("%,d", newTotal)), true);
        return 1;
    }

    private static int setCp(CommandSourceStack source, long amount) {
        WorldProgressionData data = WorldProgressionData.get(source.getLevel());
        data.setCivilizationPoints(amount);
        
        long newTotal = data.getCivilizationPoints();
        source.sendSuccess(() -> Component.literal("已將CP設定為 " + String.format("%,d", amount) + "。"), true);
        
        // 設定CP後，也應該檢查一次時代是否需要變動 (可能是降級或升級)
        AgeManager.checkAgeProgression(source.getServer());
        return 1;
    }

    private static int setEra(CommandSourceStack source, String eraName) {
        try {
            Era targetEra = Era.valueOf(eraName.toUpperCase());
            WorldProgressionData data = WorldProgressionData.get(source.getLevel());
            data.setCurrentEra(targetEra);
            
            source.sendSuccess(() -> Component.literal("已將當前時代手動設定為: ").append(targetEra.getDisplayName()), true);
            return 1;
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("錯誤：無效的時代名稱。有效名稱為: " + Arrays.toString(Era.values())));
            return 0;
        }
    }
    
    // ... (您其他的指令後端邏輯保持不變)
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