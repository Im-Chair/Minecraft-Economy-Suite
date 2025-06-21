package com.chair.economycore.client;

// 这是一个客戶端專用的資料儲存類
public class ClientMoneyData {
    // 儲存從伺服器同步過來的玩家金錢
    private static long playerMoney;

    // SteleScreen 將會呼叫這個方法來取得要顯示的餘額
    public static long getPlayerMoney() {
        return playerMoney;
    }

    // 我們的網路封包 (MoneySyncS2CPacket) 在接收到訊息後，會呼叫這個方法來更新餘額
    public static void setPlayerMoney(long money) {
        playerMoney = money;
    }
}