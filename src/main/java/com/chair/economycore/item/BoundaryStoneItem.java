package com.chair.economycore.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class BoundaryStoneItem extends Item {

    public BoundaryStoneItem(Properties pProperties) {
        super(pProperties);
    }

    // 覆寫 useOn 方法，來定義對方塊右鍵點擊的行為
    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // 確保邏輯只在伺服器端執行
        if (!pContext.getLevel().isClientSide()) {
            Player player = pContext.getPlayer();
            if (player != null) {
                // 獲取被點擊的方塊的座標
                BlockPos clickedPos = pContext.getClickedPos();
                int x = clickedPos.getX();
                int y = clickedPos.getY();
                int z = clickedPos.getZ();
                
                // 創建一條訊息
                String message = String.format("已標記座標：X=%d, Y=%d, Z=%d", x, y, z);
                
                // 將訊息發送給玩家
                player.sendSystemMessage(Component.literal(message));
            }
        }
        
        // 返回 SUCCESS 表示互動成功
        return InteractionResult.SUCCESS;
    }
}