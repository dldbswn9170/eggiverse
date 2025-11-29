package com.eggiverse.app.games;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.R;
import com.eggiverse.app.data.GameRepository;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.eggiverse.app.util.ScoreManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Doodle Jump 클론 미니게임 Activity
 * - 플레이어가 플랫폼을 이용해 위로 올라가는 게임
 * - 위로 올라갈수록 점수 증가
 * - 화면 밖으로 나가면 게임 오버
 */
public class DoodleJumpActivity extends AppCompatActivity {

    private DoodleGameView gameView;
    private TextView scoreText;
    private TextView highScoreText;
    private ScoreManager scoreManager;
    private GameViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle_jump);

        // 액션바 설정
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.doodle_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );

        scoreManager = ScoreManager.getInstance(this);
        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        scoreText = findViewById(R.id.doodle_score_text);
        highScoreText = findViewById(R.id.doodle_high_score_text);

        // 현재 캐릭터 레벨 가져오기
        int characterLevel = GameRepository.get().getGameState().getValue() != null ?
                GameRepository.get().getGameState().getValue().getLevel() : 1;

        // SurfaceView를 custom GameView로 변환
        SurfaceView surfaceView = findViewById(R.id.doodle_game_view);
        if (surfaceView != null) {
            // 기존 SurfaceView를 제거하고 custom view로 교체
            android.view.ViewGroup parent = (android.view.ViewGroup) surfaceView.getParent();
            int index = parent.indexOfChild(surfaceView);
            parent.removeView(surfaceView);

            gameView = new DoodleGameView(this, characterLevel);
            gameView.setId(R.id.doodle_game_view);
            android.view.ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            parent.addView(gameView, index, params);

            gameView.setActivity(this);
            gameView.getHolder().addCallback(gameView);
            updateScoreDisplay();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null) {
            gameView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.release();
        }
    }

    public void updateScoreDisplay() {
        runOnUiThread(() -> {
            if (gameView != null) {
                scoreText.setText("점수: " + gameView.getScore());
                highScoreText.setText("최고점수: " + scoreManager.getDoodleJumpScore());
            }
        });
    }

    public void onGameOver(int score) {
        scoreManager.saveDoodleJumpScore(score);
        int rewardCoins = score / 10;
        int rewardExp = score / 5;
        viewModel.completeMiniGame(score);
        runOnUiThread(() -> {
            new AlertDialog.Builder(this, R.style.ThemeOverlay_Eggiverse_Dialog)
                    .setTitle("게임 종료!")
                    .setMessage("점수: " + score + "\n보상: " + rewardCoins + " 코인\n경험치: " + rewardExp + " EXP")
                    .setPositiveButton("다시하기", (d, which) -> gameView.restart())
                    .setNegativeButton("닫기", (d, which) -> finish())
                    .setCancelable(false)
                    .show();
        });
    }

    /**
     * 게임 로직과 렌더링을 담당하는 SurfaceView
     */
    public static class DoodleGameView extends SurfaceView implements SurfaceHolder.Callback {
        private DoodleJumpActivity activity;
        private GameThread gameThread;
        private Doodle doodle;
        private List<Platform> platforms;
        private Random random;
        private float cameraY;
        private int score;
        private boolean isRunning;
        private int screenWidth;
        private int screenHeight;
        private Bitmap characterBitmap;
        private int characterLevel;

        // 게임 상수
        private static final float GRAVITY = 0.5f;
        private static final float JUMP_POWER = 12f;
        private static final float MOVE_SPEED = 5f;
        private static final int PLATFORM_WIDTH = 60;
        private static final int PLATFORM_HEIGHT = 15;
        private static final int PLATFORM_SPACING = 80;

        public DoodleGameView(android.content.Context context) {
            super(context);
            this.characterLevel = 1;
            initializeView();
        }

        public DoodleGameView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
            this.characterLevel = 1;
            initializeView();
        }

        public DoodleGameView(android.content.Context context, int characterLevel) {
            super(context);
            this.characterLevel = characterLevel;
            initializeView();
        }

        private void initializeView() {
            random = new Random();
            platforms = new ArrayList<>();
            isRunning = false;
            loadCharacterBitmap();
        }

        private void loadCharacterBitmap() {
            try {
                int drawableId = com.eggiverse.app.util.CharacterDrawableUtil.getCharacterDrawableId(characterLevel);
                characterBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
                // 캐릭터 크기를 게임에 맞게 조정
                if (characterBitmap != null) {
                    characterBitmap = Bitmap.createScaledBitmap(characterBitmap, 40, 40, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            screenWidth = getWidth();
            screenHeight = getHeight();
            initGame();

            gameThread = new GameThread(getHolder());
            gameThread.start();
            isRunning = true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            release();
        }

        private void initGame() {
            score = 0;
            cameraY = 0;
            doodle = new Doodle(screenWidth / 2.0f, screenHeight - 100);

            platforms.clear();
            // 초기 플랫폼들 생성
            for (int i = 0; i < 10; i++) {
                float x = random.nextInt(screenWidth - PLATFORM_WIDTH);
                float y = screenHeight - i * PLATFORM_SPACING - 100;
                platforms.add(new Platform(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT));
            }
        }

        private void update() {
            if (!isRunning || doodle == null) return;

            // 플레이어 물리 업데이트
            doodle.velocityY += GRAVITY;
            doodle.y += doodle.velocityY;

            // 화면 좌우 래핑
            if (doodle.x < 0) doodle.x = screenWidth;
            if (doodle.x > screenWidth) doodle.x = 0;

            // 카메라 업데이트 (플레이어가 화면의 위 부분에 올 때)
            if (doodle.y < cameraY + screenHeight * 0.3f) {
                float deltaY = (cameraY + screenHeight * 0.3f) - doodle.y;
                cameraY -= deltaY;
                score += (int) deltaY / 10;

                // 새로운 플랫폼 생성
                while (platforms.size() < 15) {
                    float x = random.nextInt(screenWidth - PLATFORM_WIDTH);
                    float y = platforms.get(platforms.size() - 1).y - PLATFORM_SPACING;
                    platforms.add(new Platform(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT));
                }
            }

            // 오래된 플랫폼 제거
            platforms.removeIf(p -> p.y > cameraY + screenHeight);

            // 플랫폼 충돌 감지
            for (Platform platform : platforms) {
                if (doodle.checkCollision(platform)) {
                    doodle.velocityY = -JUMP_POWER;
                }
            }

            // 게임 오버 조건
            if (doodle.y > cameraY + screenHeight) {
                isRunning = false;
                if (activity != null) {
                    activity.onGameOver(score);
                }
            }
        }

        private void render(Canvas canvas) {
            if (canvas == null) return;

            // 배경 그리기
            canvas.drawColor(Color.parseColor("#87CEEB"));

            // 플랫폼 그리기
            Paint platformPaint = new Paint();
            platformPaint.setColor(Color.GREEN);
            for (Platform platform : platforms) {
                float screenY = platform.y - cameraY;
                if (screenY >= 0 && screenY <= screenHeight) {
                    canvas.drawRect(platform.x, screenY, platform.x + platform.width,
                            screenY + platform.height, platformPaint);
                }
            }

            // 캐릭터 그리기 (비트맵)
            if (doodle != null) {
                float screenY = doodle.y - cameraY;
                if (characterBitmap != null) {
                    canvas.drawBitmap(characterBitmap, doodle.x - 20, screenY - 20, null);
                } else {
                    // 비트맵 로드 실패 시 원형으로 폴백
                    Paint doodlePaint = new Paint();
                    doodlePaint.setColor(Color.YELLOW);
                    canvas.drawCircle(doodle.x, screenY, doodle.radius, doodlePaint);
                }
            }

            // 점수 표시
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(40);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Score: " + score, 20, 50, textPaint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (doodle == null) return true;

            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    doodle.x = event.getX();
                    break;
            }
            return true;
        }

        public void setActivity(DoodleJumpActivity activity) {
            this.activity = activity;
        }

        public int getScore() {
            return score;
        }

        public void pause() {
            isRunning = false;
        }

        public void resume() {
            isRunning = true;
        }

        public void restart() {
            initGame();
            isRunning = true;
        }

        public void release() {
            isRunning = false;
            if (gameThread != null) {
                gameThread.interrupt();
            }
        }

        /**
         * 게임 렌더링 스레드
         */
        private class GameThread extends Thread {
            private SurfaceHolder surfaceHolder;
            private boolean running = true;

            public GameThread(SurfaceHolder holder) {
                surfaceHolder = holder;
            }

            @Override
            public void run() {
                while (running && !isInterrupted()) {
                    Canvas canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas != null) {
                            synchronized (surfaceHolder) {
                                update();
                                render(canvas);
                                if (activity != null) {
                                    activity.updateScoreDisplay();
                                }
                            }
                        }
                        Thread.sleep(16); // ~60 FPS
                    } catch (InterruptedException e) {
                        running = false;
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
            }

            public void stopRunning() {
                running = false;
            }
        }
    }

    /**
     * 플레이어 (Doodle) 클래스
     */
    private static class Doodle {
        float x, y;
        float velocityY;
        int radius = 15;

        Doodle(float x, float y) {
            this.x = x;
            this.y = y;
            this.velocityY = 0;
        }

        boolean checkCollision(Platform platform) {
            // 플레이어가 플랫폼 위에서 아래로 떨어질 때만 충돌 감지
            return x + radius > platform.x &&
                    x - radius < platform.x + platform.width &&
                    y + radius >= platform.y &&
                    y + radius <= platform.y + platform.height + 10 &&
                    velocityY > 0;
        }
    }

    /**
     * 플랫폼 클래스
     */
    private static class Platform {
        float x, y;
        int width, height;

        Platform(float x, float y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
