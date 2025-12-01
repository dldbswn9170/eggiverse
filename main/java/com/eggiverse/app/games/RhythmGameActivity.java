package com.eggiverse.app.games;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.R;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.eggiverse.app.util.ScoreManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 4-Key 리듬 게임 (Jmania 클론) Activity
 * - 4개 레인(좌, 하, 상, 우)에서 노트를 타이밍에 맞게 터치
 * - 정확한 타이밍: Perfect(3점), Great(2점), Good(1점), Miss(0점)
 * - 정확도와 점수를 실시간으로 표시
 */
public class RhythmGameActivity extends AppCompatActivity {

    private RhythmGameView gameView;
    private TextView scoreText;
    private TextView accuracyText;
    private ScoreManager scoreManager;
    private GameViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythm_game);

        // 액션바 설정
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.rhythm_toolbar);
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
        scoreText = findViewById(R.id.rhythm_score_text);
        accuracyText = findViewById(R.id.rhythm_accuracy_text);

        // SurfaceView를 custom GameView로 변환
        SurfaceView surfaceView = findViewById(R.id.rhythm_game_view);
        if (surfaceView != null) {
            // 기존 SurfaceView를 제거하고 custom view로 교체
            android.view.ViewGroup parent = (android.view.ViewGroup) surfaceView.getParent();
            int index = parent.indexOfChild(surfaceView);
            parent.removeView(surfaceView);

            gameView = new RhythmGameView(this);
            gameView.setId(R.id.rhythm_game_view);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (gameView != null) {
            gameView.onKeyPressed(keyCode);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void updateScoreDisplay() {
        runOnUiThread(() -> {
            if (gameView != null) {
                scoreText.setText("점수: " + gameView.getScore());
                accuracyText.setText(String.format("정확도: %.1f%%", gameView.getAccuracy()));
            }
        });
    }

    public void onGameOver(int score) {
        scoreManager.saveRhythmGameScore(score);
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
     * 리듬 게임 로직과 렌더링을 담당하는 SurfaceView
     */
    public static class RhythmGameView extends SurfaceView implements SurfaceHolder.Callback {
        private RhythmGameActivity activity;
        private GameThread gameThread;
        private List<Note> notes;
        private Random random;
        private int score;
        private int totalNotes;
        private int correctNotes;
        private boolean isRunning;
        private int screenWidth;
        private int screenHeight;
        private long gameStartTime;
        private long gameDuration;

        // 게임 상수
        private static final int LANE_COUNT = 4;
        private static final int LANE_WIDTH = 80;
        private static final int LANE_SPACING = 20;
        private static final int HIT_ZONE_Y = 600;
        private static final int HIT_ZONE_HEIGHT = 40;
        private static final int HIT_TOLERANCE_PERFECT = 30;
        private static final int HIT_TOLERANCE_GREAT = 60;
        private static final int HIT_TOLERANCE_GOOD = 90;
        private static final int NOTE_HEIGHT = 40;
        private static final int NOTE_SPEED = 4; // 픽셀/프레임

        public RhythmGameView(android.content.Context context) {
            super(context);
            initializeView();
        }

        public RhythmGameView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
            initializeView();
        }

        private void initializeView() {
            random = new Random();
            notes = new ArrayList<>();
            score = 0;
            totalNotes = 0;
            correctNotes = 0;
            isRunning = false;
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
            totalNotes = 0;
            correctNotes = 0;
            notes.clear();
            gameStartTime = System.currentTimeMillis();
            gameDuration = 30000; // 30초 게임
            generateNotes();
        }

        private void generateNotes() {
            // 30초동안 4개 레인에서 노트를 랜덤하게 생성
            // 각 레인에 약 15-20개의 노트 생성
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                for (int i = 0; i < 20; i++) {
                    long noteTime = (long) (i * (gameDuration / 20.0f));
                    int yPos = screenHeight + i * 200; // 화면 하단에서 위로 이동
                    notes.add(new Note(lane, yPos, noteTime));
                    totalNotes++;
                }
            }
        }

        private void update() {
            if (!isRunning) return;

            long elapsedTime = System.currentTimeMillis() - gameStartTime;

            // 게임 종료 조건
            if (elapsedTime > gameDuration) {
                isRunning = false;
                if (activity != null) {
                    activity.onGameOver(score);
                }
                return;
            }

            // 노트 업데이트 (위로 이동)
            List<Note> notesToRemove = new ArrayList<>();
            for (Note note : notes) {
                note.y -= NOTE_SPEED;

                // 히트 존 통과 (Miss 처리)
                if (note.y < HIT_ZONE_Y - 100 && !note.isProcessed) {
                    note.isProcessed = true;
                    // Miss는 처리하지 않음 (자동으로 넘어감)
                }

                // 화면 밖 노트 제거
                if (note.y < -NOTE_HEIGHT) {
                    notesToRemove.add(note);
                }
            }

            notes.removeAll(notesToRemove);
        }

        private void render(Canvas canvas) {
            if (canvas == null) return;

            // 배경
            canvas.drawColor(Color.parseColor("#1a1a1a"));

            // 게임 시간 표시
            long elapsedTime = System.currentTimeMillis() - gameStartTime;
            long remainingTime = Math.max(0, gameDuration - elapsedTime);
            int remainingSeconds = (int) (remainingTime / 1000);

            // 4개 레인과 노트 그리기
            Paint lanePaint = new Paint();
            lanePaint.setColor(Color.parseColor("#333333"));

            Paint notePaint = new Paint();
            notePaint.setColor(Color.parseColor("#FF6B9D"));

            int startX = 50;
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                int laneX = startX + lane * (LANE_WIDTH + LANE_SPACING);

                // 레인 배경
                canvas.drawRect(laneX, 0, laneX + LANE_WIDTH, screenHeight, lanePaint);

                // 이 레인의 노트들 그리기
                for (Note note : notes) {
                    if (note.lane == lane) {
                        // 노트 그리기 (직사각형)
                        Paint noteColorPaint = new Paint();
                        noteColorPaint.setColor(Color.parseColor("#FFD700"));
                        canvas.drawRect(laneX + 5, note.y, laneX + LANE_WIDTH - 5,
                                note.y + NOTE_HEIGHT, noteColorPaint);
                    }
                }
            }

            // 히트 존 표시 (흰색 라인)
            Paint hitZonePaint = new Paint();
            hitZonePaint.setColor(Color.WHITE);
            hitZonePaint.setStrokeWidth(3);
            int hitZoneX1 = startX;
            int hitZoneX2 = startX + LANE_COUNT * (LANE_WIDTH + LANE_SPACING);
            canvas.drawLine(hitZoneX1, HIT_ZONE_Y, hitZoneX2, HIT_ZONE_Y, hitZonePaint);

            // 점수 및 정확도 표시
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(40);
            textPaint.setStyle(Paint.Style.FILL);
            canvas.drawText("Score: " + score, 20, 80, textPaint);
            canvas.drawText("Time: " + remainingSeconds, 20, 130, textPaint);

            if (totalNotes > 0) {
                double accuracy = (double) correctNotes / totalNotes * 100;
                canvas.drawText(String.format("Accuracy: %.1f%%", accuracy), 20, 180, textPaint);
            }

            // 터치 영역 표시 (하단 4개 버튼)
            Paint buttonPaint = new Paint();
            buttonPaint.setColor(Color.parseColor("#444444"));
            int buttonY = screenHeight - 100;
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                int laneX = startX + lane * (LANE_WIDTH + LANE_SPACING);
                canvas.drawRect(laneX + 5, buttonY, laneX + LANE_WIDTH - 5,
                        buttonY + 80, buttonPaint);
            }

            Paint buttonTextPaint = new Paint();
            buttonTextPaint.setColor(Color.WHITE);
            buttonTextPaint.setTextSize(30);
            String[] laneLabels = {"L", "D", "U", "R"};
            for (int lane = 0; lane < LANE_COUNT; lane++) {
                int laneX = startX + lane * (LANE_WIDTH + LANE_SPACING);
                canvas.drawText(laneLabels[lane],
                        laneX + LANE_WIDTH / 2 - 10, buttonY + 50, buttonTextPaint);
            }
        }

        public void onKeyPressed(int keyCode) {
            if (!isRunning || notes.isEmpty()) return;

            int lane = -1;
            switch (keyCode) {
                case KeyEvent.KEYCODE_A:        // L (Left)
                    lane = 0;
                    break;
                case KeyEvent.KEYCODE_S:        // D (Down)
                    lane = 1;
                    break;
                case KeyEvent.KEYCODE_W:        // U (Up)
                    lane = 2;
                    break;
                case KeyEvent.KEYCODE_D:        // R (Right)
                    lane = 3;
                    break;
            }

            if (lane == -1) return;

            // 이 레인의 가장 가까운 (위) 노트 찾기
            Note closestNote = null;
            int minDistance = Integer.MAX_VALUE;

            for (Note note : notes) {
                if (note.lane == lane && !note.isProcessed) {
                    int distance = Math.abs(note.y - HIT_ZONE_Y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestNote = note;
                    }
                }
            }

            if (closestNote != null && minDistance < HIT_TOLERANCE_GOOD) {
                closestNote.isProcessed = true;
                int points = judgeHit(minDistance);
                score += points;
                correctNotes++;
            }
        }

        private int judgeHit(int distance) {
            if (distance <= HIT_TOLERANCE_PERFECT) {
                return 3; // Perfect
            } else if (distance <= HIT_TOLERANCE_GREAT) {
                return 2; // Great
            } else if (distance <= HIT_TOLERANCE_GOOD) {
                return 1; // Good
            }
            return 0; // Miss
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                int startX = 50;

                // 터치한 레인 판정
                for (int lane = 0; lane < LANE_COUNT; lane++) {
                    int laneX = startX + lane * (LANE_WIDTH + LANE_SPACING);
                    if (x >= laneX && x <= laneX + LANE_WIDTH) {
                        // 이 레인을 터치함
                        Note closestNote = null;
                        int minDistance = Integer.MAX_VALUE;

                        for (Note note : notes) {
                            if (note.lane == lane && !note.isProcessed) {
                                int distance = Math.abs(note.y - HIT_ZONE_Y);
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    closestNote = note;
                                }
                            }
                        }

                        if (closestNote != null && minDistance < HIT_TOLERANCE_GOOD) {
                            closestNote.isProcessed = true;
                            int points = judgeHit(minDistance);
                            score += points;
                            correctNotes++;
                        }
                        break;
                    }
                }
            }
            return true;
        }

        public void setActivity(RhythmGameActivity activity) {
            this.activity = activity;
        }

        public int getScore() {
            return score;
        }

        public double getAccuracy() {
            if (totalNotes == 0) return 0.0;
            return (double) correctNotes / totalNotes * 100.0;
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
     * 리듬 게임의 노트 클래스
     */
    private static class Note {
        int lane;           // 0: Left, 1: Down, 2: Up, 3: Right
        int y;              // Y 좌표 (위로 이동)
        long noteTime;      // 음악 상 나타나야 할 시간 (ms)
        boolean isProcessed;

        Note(int lane, int y, long noteTime) {
            this.lane = lane;
            this.y = y;
            this.noteTime = noteTime;
            this.isProcessed = false;
        }
    }
}
