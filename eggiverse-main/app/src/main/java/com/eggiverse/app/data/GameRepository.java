package com.eggiverse.app.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.eggiverse.app.data.db.AppDatabase;
import com.eggiverse.app.data.db.dao.GameStateDao;
import com.eggiverse.app.data.db.entity.GameState;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameRepository {

    private static final long HOUR_IN_MILLIS = 60 * 60 * 1000;

    private static GameRepository instance;

    private final GameStateDao gameStateDao;
    private final ExecutorService executorService;
    private final MutableLiveData<GameState> stateLiveData = new MutableLiveData<>();

    private GameRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context);
        this.gameStateDao = db.gameStateDao();
        this.executorService = Executors.newSingleThreadExecutor();
        loadInitialData();
    }

    public static void init(@NonNull Context context) {
        if (instance == null) {
            instance = new GameRepository(context.getApplicationContext());
        }
    }

    public static GameRepository get() {
        if (instance == null) {
            throw new IllegalStateException("GameRepository not initialized");
        }
        return instance;
    }

    public LiveData<GameState> getGameState() {
        return stateLiveData;
    }

    private void loadInitialData() {
        executorService.execute(() -> {
            GameState latestGameState = gameStateDao.getLatestGameState();
            if (latestGameState == null) {
                // 초기 게임 상태 생성 - 코인을 99999로 설정
                latestGameState = new GameState(System.currentTimeMillis(), 1, 1, 99999, 0, 80, 80);
                gameStateDao.insert(latestGameState);
            } else {
                updateStatsOverTime(latestGameState);
            }
            stateLiveData.postValue(latestGameState);
        });
    }

    private void updateStatsOverTime(GameState gameState) {
        long now = System.currentTimeMillis();
        long diff = now - gameState.getLastUpdated();

        if (diff > HOUR_IN_MILLIS) {
            int hoursPassed = (int) (diff / HOUR_IN_MILLIS);
            gameState.setHunger(Math.max(0, gameState.getHunger() - hoursPassed));
            gameState.setHappiness(Math.max(0, gameState.getHappiness() - hoursPassed));
            gameState.setLastUpdated(now);
            updateGameState(gameState);
        }
    }

    public void updateGameState(GameState gameState) {
        gameState.setLastUpdated(System.currentTimeMillis());
        executorService.execute(() -> {
            gameStateDao.update(gameState);
            stateLiveData.postValue(gameState);
        });
    }

    public boolean buyItem(ShopItem item) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null && currentState.getCoin() >= item.getPrice()) {
            currentState.setCoin(currentState.getCoin() - item.getPrice());
            currentState.getOwnedItems().add(item.getId());
            updateGameState(currentState);
            return true;
        }
        return false;
    }

    public void useItem(ShopItem item) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null && currentState.getOwnedItems().contains(item.getId())) {
            switch (item.getType()) {
                case FOOD:
                    currentState.setHunger(Math.min(100, currentState.getHunger() + item.getEffectValue()));
                    currentState.getOwnedItems().remove(item.getId());
                    break;
                case TOY:
                    currentState.setHappiness(Math.min(100, currentState.getHappiness() + item.getEffectValue()));
                    currentState.getOwnedItems().remove(item.getId());
                    break;
                case DECORATION:
                    currentState.getDecorations().add(item.getId());
                    break;
            }
            updateGameState(currentState);
        }
    }

    public void removeDecoration(String id) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null && currentState.getDecorations().remove(id)) {
            updateGameState(currentState);
        }
    }

    public void updateDecorations(Set<String> decorations) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null) {
            currentState.setDecorations(decorations);
            updateGameState(currentState);
        }
    }

    public void completeMiniGame(int score) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null) {
            int rewardCoins = score / 10;
            int rewardExp = 100;

            currentState.setCoin(currentState.getCoin() + rewardCoins);

            int newExp = currentState.getExp() + rewardExp;
            int requiredExp = 100;

            if (newExp >= requiredExp) {
                currentState.setLevel(currentState.getLevel() + 1);
                currentState.setExp(newExp - requiredExp);
            } else {
                currentState.setExp(newExp);
            }

            updateGameState(currentState);
        }
    }

    // 테스트용: 코인 직접 설정
    public void setCoins(int coins) {
        GameState currentState = stateLiveData.getValue();
        if (currentState != null) {
            currentState.setCoin(coins);
            updateGameState(currentState);
        }
    }
}