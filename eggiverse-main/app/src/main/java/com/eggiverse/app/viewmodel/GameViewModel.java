package com.eggiverse.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.eggiverse.app.data.GameRepository;
import com.eggiverse.app.data.ShopItem;
import com.eggiverse.app.data.db.entity.GameState;
import java.util.Set;

public class GameViewModel extends AndroidViewModel {

    private final GameRepository repository;
    private final LiveData<GameState> state;

    public GameViewModel(@NonNull Application application) {
        super(application);
        GameRepository.init(application);
        repository = GameRepository.get();
        state = repository.getGameState();
    }

    public LiveData<GameState> getState() {
        return state;
    }

    public void feedEgg(int amount) {
        GameState currentGameState = state.getValue();
        if (currentGameState != null) {
            currentGameState.setHunger(Math.min(100, currentGameState.getHunger() + amount));
            repository.updateGameState(currentGameState);
        }
    }

    public void playWithEgg(int amount) {
        GameState currentGameState = state.getValue();
        if (currentGameState != null) {
            currentGameState.setHappiness(Math.min(100, currentGameState.getHappiness() + amount));
            repository.updateGameState(currentGameState);
        }
    }

    public boolean buyItem(ShopItem item) {
        return repository.buyItem(item);
    }

    public void useItem(ShopItem item) {
        repository.useItem(item);
    }

    public void removeDecoration(String id) {
        repository.removeDecoration(id);
    }

    public void completeMiniGame(int score) {
        repository.completeMiniGame(score);
    }

    public void updateDecorations(Set<String> decorations) {
        repository.updateDecorations(decorations);
    }
}
