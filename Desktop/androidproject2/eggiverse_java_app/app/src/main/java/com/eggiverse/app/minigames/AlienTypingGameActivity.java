package com.eggiverse.app.minigames;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.R;
import com.eggiverse.app.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 외계어 타자 게임 Activity
 * - 제한시간 안에 외계어 문장을 타이핑하는 게임
 * - 문장을 모두 입력하면 번역문이 출력됨
 * - 최대 5단계 난이도까지 존재
 */
public class AlienTypingGameActivity extends AppCompatActivity {

    private AlienTypingGameView gameView;
    private EditText inputEditText;
    private TextView stageText;
    private TextView timerText;
    private TextView alienText;
    private TextView translationText;
    private ScoreManager scoreManager;
    private GameViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alien_typing_game);

        // 액션바 설정
        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.alien_toolbar);
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

        // UI 요소 초기화
        inputEditText = findViewById(R.id.alien_input_text);
        stageText = findViewById(R.id.alien_stage_text);
        timerText = findViewById(R.id.alien_timer_text);
        alienText = findViewById(R.id.alien_sentence_text);
        translationText = findViewById(R.id.alien_translation_text);

        // SurfaceView 초기화
        SurfaceView surfaceView = findViewById(R.id.alien_game_view);
        if (surfaceView != null) {
            android.view.ViewGroup parent = (android.view.ViewGroup) surfaceView.getParent();
            int index = parent.indexOfChild(surfaceView);
            parent.removeView(surfaceView);

            gameView = new AlienTypingGameView(this);
            gameView.setId(R.id.alien_game_view);
            android.view.ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
            parent.addView(gameView, index, params);

            gameView.setActivity(this);
            gameView.getHolder().addCallback(gameView);
        }

        // 입력 리스너 설정
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (gameView != null) {
                    gameView.setUserInput(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
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

    public void updateUI(int stage, long remainingTime, String alienSentence, String translation, boolean isCorrect, String userInput) {
        runOnUiThread(() -> {
            stageText.setText("Stage " + stage + "/5");
            timerText.setText("Time: " + (remainingTime / 1000) + "s");
            alienText.setText(alienSentence);

            // 입력을 완료한 경우에만 번역 표시
            if (isCorrect) {
                translationText.setText("✓ " + translation);
                translationText.setTextColor(Color.parseColor("#4CAF50"));
            } else if (userInput.length() > 0) {
                // 부분 입력 시: 일치하는 글자만 표시하고 나머지는 ?로 표시
                StringBuilder display = new StringBuilder();
                for (int i = 0; i < alienSentence.length(); i++) {
                    if (i < userInput.length() && userInput.charAt(i) == alienSentence.charAt(i)) {
                        display.append(userInput.charAt(i));
                    } else {
                        display.append("?");
                    }
                }
                translationText.setText(display.toString());
                translationText.setTextColor(Color.parseColor("#FFEB3B"));
            } else {
                // 입력하지 않음: 번역문 보이지 않음
                translationText.setText("");
                translationText.setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
    }

    public void onGameOver(int score, int stage) {
        scoreManager.saveAlienTypingScore(score);
        int rewardCoins = score / 10;
        int rewardExp = score / 5;
        viewModel.completeMiniGame(score);
        runOnUiThread(() -> {
            new AlertDialog.Builder(this, R.style.ThemeOverlay_Eggiverse_Dialog)
                    .setTitle("게임 완료!")
                    .setMessage("최종 스테이지: " + stage + "/5\n획득 점수: " + score + "\n보상: " + rewardCoins + " 코인\n경험치: " + rewardExp + " EXP")
                    .setPositiveButton("다시하기", (d, which) -> gameView.restart())
                    .setNegativeButton("닫기", (d, which) -> finish())
                    .setCancelable(false)
                    .show();
        });
    }

    public void clearInput() {
        runOnUiThread(() -> inputEditText.setText(""));
    }

    /**
     * 외계어 타자 게임 로직과 렌더링을 담당하는 SurfaceView
     */
    public static class AlienTypingGameView extends SurfaceView implements SurfaceHolder.Callback {
        private AlienTypingGameActivity activity;
        private GameThread gameThread;
        private Random random;
        private int currentStage;
        private int score;
        private boolean isRunning;
        private int screenWidth;
        private int screenHeight;
        private long gameStartTime;
        private long stageStartTime;
        private String currentAlienSentence;
        private String currentTranslation;
        private String userInput;
        private boolean isCurrentCorrect;

        // 게임 데이터
        private static final List<String[]> GAME_DATA = new ArrayList<>();
        static {
            GAME_DATA.add(new String[]{"쀼쀼빵빵", "안녕하세요"});
            GAME_DATA.add(new String[]{"뿍삣깡깡", "반갑습니다"});
            GAME_DATA.add(new String[]{"뀨뀨삥빵", "무엇입니까"});
            GAME_DATA.add(new String[]{"쮸쮸뺙뺙", "좋습니다"});
            GAME_DATA.add(new String[]{"삐삐삥삥", "감사합니다"});
            GAME_DATA.add(new String[]{"깡깡빵빵", "네 알겠습니다"});
            GAME_DATA.add(new String[]{"뻐뻐쀍쀍", "아름답습니다"});
            GAME_DATA.add(new String[]{"삡삡쮸쮸", "너를 좋아해"});
            GAME_DATA.add(new String[]{"빵삐깡뺙", "별빛이 예쁘다"});
            GAME_DATA.add(new String[]{"쀼삥빵쀍", "지금이 소중해"});
        }

        // 난이도별 설정
        private static final long[] TIME_LIMITS = {15000, 13000, 11000, 9000, 7000}; // 스테이지별 시간 제한 (ms)
        private static final int[] SENTENCE_LENGTHS = {4, 5, 6, 7, 8}; // 스테이지별 문장 길이

        public AlienTypingGameView(android.content.Context context) {
            super(context);
            initializeView();
        }

        public AlienTypingGameView(android.content.Context context, android.util.AttributeSet attrs) {
            super(context, attrs);
            initializeView();
        }

        private void initializeView() {
            random = new Random();
            currentStage = 1;
            score = 0;
            isRunning = false;
            userInput = "";
            isCurrentCorrect = false;
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
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            release();
        }

        private void initGame() {
            currentStage = 1;
            score = 0;
            gameStartTime = System.currentTimeMillis();
            loadNextStage();
        }

        private void loadNextStage() {
            stageStartTime = System.currentTimeMillis();

            // 무작위로 문장 선택 (중복 제거를 위해 충분한 데이터 필요)
            int randomIndex = random.nextInt(GAME_DATA.size());
            String[] sentence = GAME_DATA.get(randomIndex);
            currentAlienSentence = sentence[0];
            currentTranslation = sentence[1];
            userInput = "";
            isCurrentCorrect = false;

            if (activity != null) {
                activity.updateUI(currentStage, getTimeLimit(), currentAlienSentence, currentTranslation, false, "");
                activity.clearInput();
            }
        }

        public void setUserInput(String input) {
            userInput = input;

            // 올바른 입력인지 확인
            isCurrentCorrect = userInput.equals(currentAlienSentence);

            if (activity != null) {
                activity.updateUI(currentStage, getRemainingTime(), currentAlienSentence, currentTranslation, isCurrentCorrect, userInput);
            }

            // 정확히 문장을 모두 입력하면 다음 스테이지로
            if (isCurrentCorrect) {
                score += 100 * currentStage;

                // 약간의 딜레이 후 다음 스테이지로 (사용자가 성공을 볼 수 있도록)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (currentStage < 5) {
                    currentStage++;
                    loadNextStage();
                } else {
                    // 5스테이지 완료
                    isRunning = false;
                    if (activity != null) {
                        activity.onGameOver(score, currentStage);
                    }
                }
            }
        }

        private void update() {
            if (!isRunning) return;

            long remainingTime = getRemainingTime();

            // 시간 초과
            if (remainingTime <= 0) {
                isRunning = false;
                if (activity != null) {
                    activity.onGameOver(score, currentStage);
                }
            }
        }

        private void render(Canvas canvas) {
            if (canvas == null) return;

            // 배경
            canvas.drawColor(Color.parseColor("#1a1a2e"));

            // 현재 진행 상태 표시
            Paint textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(50);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setStyle(Paint.Style.FILL);

            // 스테이지 표시
            canvas.drawText("Stage " + currentStage + "/5", screenWidth / 2, 100, textPaint);

            // 남은 시간
            long remainingTime = getRemainingTime();
            int timeSeconds = (int) (remainingTime / 1000);
            textPaint.setTextSize(40);
            textPaint.setColor(remainingTime < 3000 ? Color.RED : Color.YELLOW);
            canvas.drawText("Time: " + timeSeconds + "s", screenWidth / 2, 200, textPaint);

            // 외계어 문장
            textPaint.setColor(Color.parseColor("#64B5F6"));
            textPaint.setTextSize(60);
            canvas.drawText(currentAlienSentence, screenWidth / 2, 350, textPaint);

            // 입력 진행도
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(35);
            canvas.drawText("입력: " + userInput, screenWidth / 2, 450, textPaint);

            // 진행도 표시 (입력한 글자 / 필요한 글자)
            textPaint.setColor(Color.parseColor("#64B5F6"));
            textPaint.setTextSize(25);
            canvas.drawText("(" + userInput.length() + "/" + currentAlienSentence.length() + ")", screenWidth / 2, 490, textPaint);

            // 번역문 (정확히 입력했을 때만 표시)
            if (isCurrentCorrect) {
                textPaint.setColor(Color.parseColor("#4CAF50"));
                textPaint.setTextSize(45);
                canvas.drawText("✓ " + currentTranslation, screenWidth / 2, 570, textPaint);
            } else if (userInput.length() > 0) {
                // 부분 입력 시: 진행도 표시
                StringBuilder display = new StringBuilder();
                for (int i = 0; i < currentAlienSentence.length(); i++) {
                    if (i < userInput.length() && userInput.charAt(i) == currentAlienSentence.charAt(i)) {
                        display.append("✓ ");
                    } else {
                        display.append("○ ");
                    }
                }
                textPaint.setColor(Color.parseColor("#FFEB3B"));
                textPaint.setTextSize(25);
                canvas.drawText(display.toString(), screenWidth / 2, 570, textPaint);
            } else {
                // 입력하지 않음: 안내문
                textPaint.setColor(Color.parseColor("#999999"));
                textPaint.setTextSize(30);
                canvas.drawText("위의 외계어를 입력하세요", screenWidth / 2, 570, textPaint);
            }

            // 점수
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(30);
            canvas.drawText("Score: " + score, screenWidth / 2, 650, textPaint);
        }

        private long getTimeLimit() {
            return TIME_LIMITS[Math.min(currentStage - 1, TIME_LIMITS.length - 1)];
        }

        private long getRemainingTime() {
            long elapsed = System.currentTimeMillis() - stageStartTime;
            long remaining = getTimeLimit() - elapsed;
            return Math.max(0, remaining);
        }

        public void setActivity(AlienTypingGameActivity activity) {
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
}
