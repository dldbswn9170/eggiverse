package com.eggiverse.app.games;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.R;
import com.eggiverse.app.databinding.ActivityStarTapBinding;
import com.eggiverse.app.viewmodel.GameViewModel;

import java.util.Random;

public class StarTapGameActivity extends AppCompatActivity {

    private ActivityStarTapBinding binding;
    private GameViewModel viewModel;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Random random = new Random();
    private int score = 0;
    private int timeLeft = 30;
    private boolean playing = false;

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (!playing) return;
            timeLeft--;
            binding.timerText.setText("시간: " + timeLeft);
            if (timeLeft <= 0) {
                endGame();
            } else {
                handler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStarTapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setSupportActionBar(binding.starToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.starToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
        binding.starStartButton.setOnClickListener(v -> startGame());
    }

    private void startGame() {
        score = 0;
        timeLeft = 30;
        playing = true;

        binding.scoreText.setText("점수: " + score);
        binding.timerText.setText("시간: " + timeLeft);
        binding.starGameArea.removeAllViews();

        handler.removeCallbacks(timerRunnable);
        handler.postDelayed(timerRunnable, 1000);

        binding.starGameArea.post(this::spawnStars);
    }

    private void spawnStars() {
        if (!playing) return;

        binding.starGameArea.removeAllViews();
        int width = binding.starGameArea.getWidth();
        int height = binding.starGameArea.getHeight();
        if (width == 0 || height == 0) {
            binding.starGameArea.post(this::spawnStars);
            return;
        }

        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(this);
            star.setImageResource(android.R.drawable.btn_star_big_on);
            star.setColorFilter(getColor(R.color.amber_accent));
            int size = 100;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = random.nextInt(Math.max(1, width - size));
            params.topMargin = random.nextInt(Math.max(1, height - size));
            star.setLayoutParams(params);
            star.setOnClickListener(v -> onStarTapped((ImageView) v));
            binding.starGameArea.addView(star);
        }
    }

    private void onStarTapped(ImageView star) {
        if (!playing) return;
        score += 10;
        binding.scoreText.setText("점수: " + score);

        int width = binding.starGameArea.getWidth();
        int height = binding.starGameArea.getHeight();
        int size = star.getLayoutParams().width;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) star.getLayoutParams();
        params.leftMargin = random.nextInt(Math.max(1, width - size));
        params.topMargin = random.nextInt(Math.max(1, height - size));
        star.setLayoutParams(params);
    }

    private void endGame() {
        playing = false;
        handler.removeCallbacks(timerRunnable);
        viewModel.completeMiniGame(score);

        new AlertDialog.Builder(this, R.style.ThemeOverlay_Eggiverse_Dialog)
                .setTitle("게임 종료!")
                .setMessage("점수: " + score + "\n보상: " + (score / 10) + " 코인")
                .setPositiveButton("다시하기", (d, which) -> startGame())
                .setNegativeButton("닫기", (d, which) -> finish())
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}

