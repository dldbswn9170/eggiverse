package com.eggiverse.app.util;

import android.content.Context;
import android.media.MediaPlayer;

import com.eggiverse.app.R;

public class BackgroundMusicManager {
    private static BackgroundMusicManager instance;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;

    private BackgroundMusicManager() {}

    public static BackgroundMusicManager getInstance() {
        if (instance == null) {
            instance = new BackgroundMusicManager();
        }
        return instance;
    }

    /**
     * 배경음악 시작
     */
    public void start(Context context) {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.bgm_main);
                mediaPlayer.setLooping(true); // 무한 반복
                mediaPlayer.setVolume(1.0f, 1.0f); // 볼륨 30% (너무 크지 않게)
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        if (!mediaPlayer.isPlaying() && !isPaused) {
            mediaPlayer.start();
        }
    }

    /**
     * 일시정지
     */
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    /**
     * 재개
     */
    public void resume() {
        if (mediaPlayer != null && isPaused) {
            mediaPlayer.start();
            isPaused = false;
        }
    }

    /**
     * 정지
     */
    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPaused = false;
        }
    }

    /**
     * 볼륨 조절 (0.0 ~ 1.0)
     */
    public void setVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 재생 중인지 확인
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}
