package com.eggiverse.app.inventory;

public class Toy {
    private String name;
    private String description;
    private int happinessValue;

    public Toy(String name, String description, int happinessValue) {
        this.name = name;
        this.description = description;
        this.happinessValue = happinessValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getHappinessValue() {
        return happinessValue;
    }
}
