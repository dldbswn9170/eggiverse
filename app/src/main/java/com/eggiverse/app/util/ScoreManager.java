package com.eggiverse.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class ScoreManager {

    private static ScoreManager instance;
    private final SharedPreferences prefs;

    private static final String PREFS_NAME = "game_scores";
    private static final String KEY_DOODLE_JUMP = "doodle_jump_high_score";
    private static final String KEY_FLAPPY_BIRD = "flappy_bird_high_score";
    private static final String KEY_RHYTHM_GAME = "rhythm_game_high_score";
    private static final String KEY_ALIEN_TYPING = "alien_typing_high_score";

    private ScoreManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ScoreManager getInstance(Context context) {
        if (instance == null) {
            instance = new ScoreManager(context.getApplicationContext());
        }
        return instance;
    }

    public void saveDoodleJumpScore(int score) {
        if (score > getDoodleJumpScore()) {
            prefs.edit().putInt(KEY_DOODLE_JUMP, score).apply();
        }
    }

    public int getDoodleJumpScore() {
        return prefs.getInt(KEY_DOODLE_JUMP, 0);
    }

    public void saveFlappyBirdScore(int score) {
        if (score > getFlappyBirdScore()) {
            prefs.edit().putInt(KEY_FLAPPY_BIRD, score).apply();
        }
    }

    public int getFlappyBirdScore() {
        return prefs.getInt(KEY_FLAPPY_BIRD, 0);
    }

    public void saveRhythmGameScore(int score) {
        if (score > getRhythmGameScore()) {
            prefs.edit().putInt(KEY_RHYTHM_GAME, score).apply();
        }
    }

    public int getRhythmGameScore() {
        return prefs.getInt(KEY_RHYTHM_GAME, 0);
    }

    public void saveAlienTypingScore(int score) {
        if (score > getAlienTypingScore()) {
            prefs.edit().putInt(KEY_ALIEN_TYPING, score).apply();
        }
    }

    public int getAlienTypingScore() {
        return prefs.getInt(KEY_ALIEN_TYPING, 0);
    }
}
