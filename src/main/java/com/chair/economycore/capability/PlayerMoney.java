package com.chair.economycore.capability;

public class PlayerMoney {
    private long money;

    public long getMoney() {
        return this.money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public void addMoney(long amount) {
        this.money += amount;
    }

    public void removeMoney(long amount) {
        this.money -= amount;
        if (this.money < 0) {
            this.money = 0;
        }
    }
    
    // 用於玩家死亡後數據複製
    public void copyFrom(PlayerMoney source) {
        this.money = source.money;
    }
}