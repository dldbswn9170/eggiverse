package com.eggiverse.app.data;

import org.json.JSONException;
import org.json.JSONObject;

public class EggModel {
    private int level;
    private int experience;
    private int hunger;
    private int happiness;
    private long lastFeedTime;
    private long birthTime;

    public EggModel() {
        this.level = 1;
        this.experience = 0;
        this.hunger = 100;
        this.happiness = 100;
        long now = System.currentTimeMillis();
        this.lastFeedTime = now;
        this.birthTime = now;
    }

    public EggModel(int level, int experience, int hunger, int happiness, long lastFeedTime, long birthTime) {
        this.level = level;
        this.experience = experience;
        this.hunger = hunger;
        this.happiness = happiness;
        this.lastFeedTime = lastFeedTime;
        this.birthTime = birthTime;
    }

    public void addExperience(int exp) {
        experience += exp;
        while (experience >= getRequiredExp()) {
            experience -= getRequiredExp();
            level++;
        }
    }

    public int getRequiredExp() {
        return level * 100;
    }

    public void feed(int amount) {
        hunger = clamp(hunger + amount);
        happiness = clamp(happiness + 5);
        lastFeedTime = System.currentTimeMillis();
    }

    public void play(int amount) {
        happiness = clamp(happiness + amount);
        hunger = clamp(hunger - 5);
    }

    public void updateStatus() {
        long now = System.currentTimeMillis();
        long hours = Math.max(0, (now - lastFeedTime) / (1000 * 60 * 60));
        hunger = clamp(hunger - (int) (hours * 5));
        happiness = clamp(happiness - (int) (hours * 3));
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(100, value));
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("level", level);
        object.put("experience", experience);
        object.put("hunger", hunger);
        object.put("happiness", happiness);
        object.put("lastFeedTime", lastFeedTime);
        object.put("birthTime", birthTime);
        return object;
    }

    public static EggModel fromJson(JSONObject object) {
        return new EggModel(
                object.optInt("level", 1),
                object.optInt("experience", 0),
                object.optInt("hunger", 100),
                object.optInt("happiness", 100),
                object.optLong("lastFeedTime", System.currentTimeMillis()),
                object.optLong("birthTime", System.currentTimeMillis())
        );
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getHunger() {
        return hunger;
    }

    public int getHappiness() {
        return happiness;
    }

    public long getLastFeedTime() {
        return lastFeedTime;
    }

    public long getBirthTime() {
        return birthTime;
    }
}

