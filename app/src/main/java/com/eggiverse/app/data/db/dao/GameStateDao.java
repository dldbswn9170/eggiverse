package com.eggiverse.app.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.eggiverse.app.data.db.entity.GameState;

import java.util.List;

@Dao
public interface GameStateDao {

    @Query("SELECT * FROM gamestate")
    List<GameState> getAll();

    @Query("SELECT * FROM gamestate ORDER BY id DESC LIMIT 1")
    GameState getLatestGameState();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GameState gameState);

    @Update
    void update(GameState gameState);
}
