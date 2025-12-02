package com.eggiverse.app;

import android.app.Application;

import com.eggiverse.app.data.GameRepository;
import com.eggiverse.app.util.BackgroundMusicManager;

public class EggiverseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Repository 초기화
        GameRepository.init(this);
        
        // 배경음악 시작
        BackgroundMusicManager.getInstance().start(this);
    }

    @Override
    public void onTerminate() {
        // 앱 종료 시 음악 정지
        BackgroundMusicManager.getInstance().stop();
        super.onTerminate();
    }
}
