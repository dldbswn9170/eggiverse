package com.eggiverse.app.data.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.HashSet;
import java.util.Set;

@Entity(tableName = "gamestate")
public class GameState {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long birthTime;
    private int dDay;
    private int level;
    private int coin;
    private int exp;
    private int hunger;
    private int happiness;
    private long lastUpdated;
    private Set<String> ownedItems = new HashSet<>();
    private Set<String> decorations = new HashSet<>();

    public GameState(long birthTime, int dDay, int level, int coin, int exp, int hunger, int happiness) {
        this.birthTime = birthTime;
        this.dDay = dDay;
        this.level = level;
        this.coin = coin;
        this.exp = exp;
        this.hunger = hunger;
        this.happiness = happiness;
        this.lastUpdated = System.currentTimeMillis();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBirthTime() {
        return birthTime;
    }

    public int getDDay() {
        return dDay;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getHappiness() {
        return happiness;
    }

    public void setHappiness(int happiness) {
        this.happiness = happiness;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Set<String> getOwnedItems() {
        return ownedItems;
    }

    public void setOwnedItems(Set<String> ownedItems) {
        this.ownedItems = ownedItems;
    }

    public Set<String> getDecorations() {
        return decorations;
    }

    public void setDecorations(Set<String> decorations) {
        this.decorations = decorations;
    }
}
