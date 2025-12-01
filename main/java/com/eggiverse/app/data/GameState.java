package com.eggiverse.app.data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GameState {
    private final EggModel egg;
    private final int coins;
    private final Set<String> ownedItems;
    private final Set<String> decorations;

    public GameState(EggModel egg, int coins, Set<String> ownedItems, Set<String> decorations) {
        this.egg = egg;
        this.coins = coins;
        this.ownedItems = Collections.unmodifiableSet(new HashSet<>(ownedItems));
        this.decorations = Collections.unmodifiableSet(new HashSet<>(decorations));
    }

    public EggModel getEgg() {
        return egg;
    }

    public int getCoins() {
        return coins;
    }

    public Set<String> getOwnedItems() {
        return ownedItems;
    }

    public Set<String> getDecorations() {
        return decorations;
    }
}

