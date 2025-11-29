package com.eggiverse.app.games;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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
 * Flappy Bird 클론 미니게임 Activity
 * - 새를 조종하여 파이프 사이로 통과
 * - 파이프를 통과할 때마다 점수 증가
 * - 파이프나 바닥에 충돌하면 게임 오버
 */
public class FlappyBirdActivity extends AppCompatActivity {

    private FlappyBirdGameView gameView;
    private ScoreManager scoreManager;
    private GameViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flappy_bird);

        // 액션바 설정
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.flappy_toolbar);
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

        // 현재 캐릭터 레벨 가져오기
        int characterLevel = GameRepository.get().getGameState().getValue() != null ?
                GameRepository.get().getGameState().getValue().getLevel() : 1;

        // SurfaceView를 custom GameView로 변환
        SurfaceView surfaceView = findViewById(R.id.flappy_game_view);
        if (surfaceView != null) {
            // 기존 SurfaceView를 제거하고 custom view로 교체
            android.view.ViewGroup parent = (android.view.ViewGroup) surfaceView.getParent();
            int index = parent.indexOfChild(surfaceView);
            parent.removeView(surfaceView);

            gameView = new FlappyBirdGameView(this, characterLevel);
            gameView.setId(R.id.flappy_game_view);
            android.view.ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            parent.addView(gameView, index, params);

            gameView.setActivity(this);
            gameView.getHolder().addCallback(gameView);
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

    public void onGameOver(int score) {
        scoreManager.saveFlappyBirdScore(score);
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
     * Flappy Bird 게임 로직과 렌더링을 담당하는 SurfaceView
     */
    public static class FlappyBirdGameView extends SurfaceView implements SurfaceHolder.Callback {
        private FlappyBirdActivity activity;
        private GameThread gameThread;
        private Bird bird;
        private List<Pipe> pipes;
        private Random random;
        private int score;
        private boolean isRunning;
        private boolean gameOver;
        private int screenWidth;
        private int screenHeight;
        private Bitmap characterBitmap;
        private int characterLevel;

        // 게임 상수
        private static final int GRAVITY = 1;
        private static final int JUMP_POWER = -9;
        private static final int PIPE_VELOCITY = -4;
        private static final int PIPE_WIDTH = 80;
        private static final int PIPE_SPACING = 200;
        private static final int OPENING_SIZE = 160;

        public FlappyBirdGameView(android.content.Context context) {
            super(context);
            this.characterLevel = 1;
            initializeView();
        }

        public FlappyBirdGameView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
            this.characterLevel = 1;
            initializeView();
        }

        public FlappyBirdGameView(android.content.Context context, int characterLevel) {
            super(context);
            this.characterLevel = characterLevel;
            initializeView();
        }

        private void initializeView() {
            random = new Random();
            pipes = new ArrayList<>();
            score = 0;
            gameOver = false;
            isRunning = false;
            loadCharacterBitmap();
        }

        private void loadCharacterBitmap() {
            try {
                int drawableId = com.eggiverse.app.util.CharacterDrawableUtil.getCharacterDrawableId(characterLevel);
                characterBitmap = BitmapFactory.decodeResource(getResources(), drawableId);
                // 캐릭터 크기를 게임에 맞게 조정 (40x40)
                if (characterBitmap != null) {
                    characterBitmap = Bitmap.createScaledBitmap(characterBitmap, 50, 50, true);
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
            gameOver = false;
            bird = new Bird(screenWidth / 4, screenHeight / 2);
            pipes.clear();

            // 초기 파이프 생성
            for (int i = 0; i < 3; i++) {
                placePipe(screenWidth + i * PIPE_SPACING);
            }
        }

        private void placePipe(int x) {
            // 위/아래 파이프 사이 랜덤 위치 결정
            int minPipeY = 100;
            int maxPipeY = screenHeight - 100 - OPENING_SIZE;
            int randomPipeY = random.nextInt(Math.max(1, maxPipeY - minPipeY + 1)) + minPipeY;

            // 위쪽 파이프
            Pipe topPipe = new Pipe(x, randomPipeY - PIPE_SPACING, PIPE_WIDTH, randomPipeY);
            pipes.add(topPipe);

            // 아래쪽 파이프
            Pipe bottomPipe = new Pipe(x, randomPipeY + OPENING_SIZE, PIPE_WIDTH,
                    screenHeight - randomPipeY - OPENING_SIZE);
            pipes.add(bottomPipe);
        }

        private void update() {
            if (gameOver) return;

            // 새 물리 업데이트
            bird.velocityY += GRAVITY;
            bird.y += bird.velocityY;

            // 바닥 충돌
            if (bird.y + bird.height >= screenHeight) {
                gameOver = true;
                if (activity != null) {
                    activity.onGameOver(score);
                }
                return;
            }

            // 천장 충돌
            if (bird.y <= 0) {
                bird.y = 0;
            }

            // 파이프 업데이트
            List<Pipe> pipesToRemove = new ArrayList<>();
            for (Pipe pipe : pipes) {
                pipe.x += PIPE_VELOCITY;

                // 파이프 통과 (점수 증가)
                if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                    score += 1;
                    pipe.passed = true;
                }

                // 화면 밖 파이프 제거
                if (pipe.x + pipe.width < 0) {
                    pipesToRemove.add(pipe);
                }
            }

            pipes.removeAll(pipesToRemove);

            // 새로운 파이프 생성
            if (pipes.isEmpty() || pipes.get(pipes.size() - 1).x < screenWidth - PIPE_SPACING) {
                placePipe(screenWidth);
            }

            // 충돌 감지
            for (Pipe pipe : pipes) {
                if (checkCollision(bird, pipe)) {
                    gameOver = true;
                    if (activity != null) {
                        activity.onGameOver(score);
                    }
                    return;
                }
            }
        }

        private boolean checkCollision(Bird bird, Pipe pipe) {
            return bird.x < pipe.x + pipe.width &&
                    bird.x + bird.width > pipe.x &&
                    bird.y < pipe.y + pipe.height &&
                    bird.y + bird.height > pipe.y;
        }

        private void render(Canvas canvas) {
            if (canvas == null) return;

            // 배경 (하늘색)
            canvas.drawColor(Color.parseColor("#87CEEB"));

            // 바닥 (초록색)
            Paint groundPaint = new Paint();
            groundPaint.setColor(Color.parseColor("#228B22"));
            canvas.drawRect(0, screenHeight - 20, screenWidth, screenHeight, groundPaint);

            // 파이프 그리기
            Paint pipePaint = new Paint();
            pipePaint.setColor(Color.parseColor("#00AA00"));
            for (Pipe pipe : pipes) {
                canvas.drawRect(pipe.x, pipe.y, pipe.x + pipe.width,
                        pipe.y + pipe.height, pipePaint);
            }

            // 캐릭터 그리기 (비트맵)
            if (characterBitmap != null) {
                canvas.drawBitmap(characterBitmap, bird.x, bird.y, null);
            } else {
                // 비트맵 로드 실패 시 원형으로 폴백
                Paint birdPaint = new Paint();
                birdPaint.setColor(Color.YELLOW);
                canvas.drawCircle(bird.x + bird.width / 2, bird.y + bird.height / 2,
                        bird.width / 2, birdPaint);
            }

            // 점수 표시
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(48);
            textPaint.setStyle(Paint.Style.STROKE);
            if (gameOver) {
                canvas.drawText("GAME OVER: " + score, 50, 100, textPaint);
            } else {
                canvas.drawText("Score: " + score, 50, 100, textPaint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (gameOver) {
                    restart();
                } else {
                    bird.velocityY = JUMP_POWER;
                }
            }
            return true;
        }

        public void setActivity(FlappyBirdActivity activity) {
            this.activity = activity;
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
     * 새 클래스
     */
    private static class Bird {
        int x, y;
        int width = 40;
        int height = 40;
        int velocityY = 0;

        Bird(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 파이프 클래스
     */
    private static class Pipe {
        int x, y, width, height;
        boolean passed = false;

        Pipe(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
