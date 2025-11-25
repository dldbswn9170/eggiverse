package com.eggiverse.app;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.data.EggModel;
import com.eggiverse.app.data.GameState;
import com.eggiverse.app.R;
import com.eggiverse.app.databinding.ActivityMainBinding;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GameViewModel viewModel;
    private ObjectAnimator eggAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setupStaticUi();
        observeGameState();
        setupListeners();
    }

    private void setupStaticUi() {
        configureStatCard(binding.cardDDay.getRoot(), "D-Day");
        configureStatCard(binding.cardLevel.getRoot(), "레벨");
        configureStatCard(binding.cardCoin.getRoot(), "코인");
        configureStatCard(binding.cardExp.getRoot(), "경험치");

        configureProgress(binding.progressHunger.getRoot(), "배고픔", R.color.amber_accent);
        configureProgress(binding.progressHappiness.getRoot(), "행복도", R.color.pink_accent);

        eggAnimator = ObjectAnimator.ofFloat(binding.eggImage, View.TRANSLATION_Y, 0f, -20f);
        eggAnimator.setDuration(2000);
        eggAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        eggAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        eggAnimator.start();
    }

    private void configureStatCard(View root, String label) {
        TextView labelView = root.findViewById(com.eggiverse.app.R.id.statLabel);
        labelView.setText(label);
    }

    private void configureProgress(View root, String label, int colorRes) {
        TextView labelView = root.findViewById(com.eggiverse.app.R.id.progressLabel);
        labelView.setText(label);
        ProgressBar bar = root.findViewById(com.eggiverse.app.R.id.progressBar);
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(colorRes)));
    }

    private void observeGameState() {
        viewModel.getState().observe(this, this::renderState);
    }

    private void renderState(GameState state) {
        if (state == null) return;
        EggModel egg = state.getEgg();

        long days = Math.max(0, (System.currentTimeMillis() - egg.getBirthTime()) / (1000 * 60 * 60 * 24));
        setStatValue(binding.cardDDay.getRoot(), String.format(Locale.getDefault(), "D+%d", days));
        setStatValue(binding.cardLevel.getRoot(), String.valueOf(egg.getLevel()));
        setStatValue(binding.cardCoin.getRoot(), String.valueOf(state.getCoins()));
        setStatValue(binding.cardExp.getRoot(), String.format(Locale.getDefault(), "%d/%d", egg.getExperience(), egg.getRequiredExp()));

        // Update egg character drawable based on level
        updateEggDrawable(egg.getLevel());

        setProgress(binding.progressHunger.getRoot(), egg.getHunger());
        setProgress(binding.progressHappiness.getRoot(), egg.getHappiness());
    }

    private void updateEggDrawable(int level) {
        int drawableId;
        if (level >= 3) {
            drawableId = R.drawable.level2;
        } else if (level >= 2) {
            drawableId = R.drawable.level1;
        } else {
            drawableId = R.drawable.pixel_egg;
        }
        binding.eggImage.setImageResource(drawableId);
    }

    private void setStatValue(View root, String value) {
        TextView valueView = root.findViewById(com.eggiverse.app.R.id.statValue);
        valueView.setText(value);
    }

    private void setProgress(View root, int value) {
        ProgressBar bar = root.findViewById(com.eggiverse.app.R.id.progressBar);
        TextView valueView = root.findViewById(com.eggiverse.app.R.id.progressValue);
        bar.setProgress(value);
        valueView.setText(String.format(Locale.getDefault(), "%d%%", value));
    }

    private void setupListeners() {
        binding.feedButton.setOnClickListener(v -> {
            viewModel.feedEgg(20);
            Snackbar.make(binding.getRoot(), "먹이를 주었습니다! 배고픔 +20", Snackbar.LENGTH_SHORT).show();
        });

        binding.playButton.setOnClickListener(v -> {
            viewModel.playWithEgg(20);
            Snackbar.make(binding.getRoot(), "놀아줬습니다! 행복도 +20", Snackbar.LENGTH_SHORT).show();
        });

        binding.eggImage.setOnClickListener(v -> {
            viewModel.playWithEgg(5);
            Snackbar.make(binding.getRoot(), "알이 좋아합니다! 행복도 +5", Snackbar.LENGTH_SHORT).show();
        });

        binding.shopNav.setOnClickListener(v -> startActivity(new android.content.Intent(this, ShopActivity.class)));
        binding.gamesNav.setOnClickListener(v -> startActivity(new android.content.Intent(this, GameSelectActivity.class)));
        binding.settingsNav.setOnClickListener(v -> startActivity(new android.content.Intent(this, SettingsActivity.class)));
        binding.settingsNav.setOnLongClickListener(v -> {
            startActivity(new android.content.Intent(this, MyRoomActivity.class));
            return true;
        });
    }

    @Override
    protected void onDestroy() {
        if (eggAnimator != null) {
            eggAnimator.cancel();
        }
        super.onDestroy();
    }
}

