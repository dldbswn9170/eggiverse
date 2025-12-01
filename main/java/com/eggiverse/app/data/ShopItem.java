package com.eggiverse.app.data;

public class ShopItem {
    public enum ItemType {
        FOOD,
        TOY,
        DECORATION
    }

    private final String id;
    private final String name;
    private final String description;
    private final int price;
    private final ItemType type;
    private final int effectValue;

    public ShopItem(String id, String name, String description, int price, ItemType type, int effectValue) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.type = type;
        this.effectValue = effectValue;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public ItemType getType() {
        return type;
    }

    public int getEffectValue() {
        return effectValue;
    }
}

