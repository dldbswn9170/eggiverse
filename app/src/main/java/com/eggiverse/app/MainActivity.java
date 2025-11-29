package com.eggiverse.app;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.chat.ChatService;
import com.eggiverse.app.data.db.entity.GameState;
import com.eggiverse.app.databinding.ActivityMainBinding;
import com.eggiverse.app.event.RandomEvent;
import com.eggiverse.app.event.RandomEventDialog;
import com.eggiverse.app.event.RandomEventManager;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private GameViewModel viewModel;
    private ObjectAnimator eggAnimator;
    private RandomEventManager eventManager;
    private ChatService chatService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        eventManager = RandomEventManager.getInstance(this);
        chatService = new ChatService();

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
        TextView labelView = root.findViewById(R.id.statLabel);
        labelView.setText(label);
    }

    private void configureProgress(View root, String label, int colorRes) {
        TextView labelView = root.findViewById(R.id.progressLabel);
        labelView.setText(label);
        ProgressBar bar = root.findViewById(R.id.progressBar);
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(getColor(colorRes)));
    }

    private void observeGameState() {
        viewModel.getState().observe(this, this::renderState);
    }

    private void renderState(GameState state) {
        if (state == null) return;

        setStatValue(binding.cardDDay.getRoot(), String.format(Locale.getDefault(), "D+%d", state.getDDay()));
        setStatValue(binding.cardLevel.getRoot(), String.valueOf(state.getLevel()));
        setStatValue(binding.cardCoin.getRoot(), String.valueOf(state.getCoin()));
        setStatValue(binding.cardExp.getRoot(), String.format(Locale.getDefault(), "%d/%d", state.getExp(), 100));

        updateEggDrawable(state.getLevel());

        setProgress(binding.progressHunger.getRoot(), state.getHunger());
        setProgress(binding.progressHappiness.getRoot(), state.getHappiness());
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
        TextView valueView = root.findViewById(R.id.statValue);
        valueView.setText(value);
    }

    private void setProgress(View root, int value) {
        ProgressBar bar = root.findViewById(R.id.progressBar);
        TextView valueView = root.findViewById(R.id.progressValue);
        bar.setProgress(value);
        valueView.setText(String.format(Locale.getDefault(), "%d%%", value));
    }

    private void setupListeners() {
        binding.feedButton.setOnClickListener(v -> {
            checkAndShowEventForFeed(() -> {
                viewModel.feedEgg(20);
                Snackbar.make(binding.getRoot(), "먹이를 주었습니다! 배고픔 +20", Snackbar.LENGTH_SHORT).show();
            });
        });

        binding.playButton.setOnClickListener(v -> {
            viewModel.playWithEgg(20);
            Snackbar.make(binding.getRoot(), "놀아줬습니다! 행복도 +20", Snackbar.LENGTH_SHORT).show();
        });

        binding.eggImage.setOnClickListener(v -> {
            viewModel.playWithEgg(5);
            Snackbar.make(binding.getRoot(), "알이 좋아합니다! 행복도 +5", Snackbar.LENGTH_SHORT).show();
        });

        binding.shopNav.setOnClickListener(v -> startActivity(new Intent(this, ShopActivity.class)));
        binding.gamesNav.setOnClickListener(v -> startActivity(new Intent(this, GameSelectActivity.class)));
        binding.myRoomNav.setOnClickListener(v -> startActivity(new Intent(this, MyRoomActivity.class)));
        binding.settingsNav.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        // Chat UI Listeners
        binding.chatBalloon.setOnClickListener(v -> binding.chatModalOverlay.setVisibility(View.VISIBLE));
        binding.closeModalButton.setOnClickListener(v -> binding.chatModalOverlay.setVisibility(View.GONE));
        binding.chatModalOverlay.setOnClickListener(v -> binding.chatModalOverlay.setVisibility(View.GONE));
        binding.sendMessageButton.setOnClickListener(v -> handleSendMessage());
    }

    private void handleSendMessage() {
        String message = binding.chatInput.getText().toString().trim();
        if (message.isEmpty()) {
            return;
        }

        addMessageToChat(message, true); // Add user message to UI
        binding.chatInput.setText("");

        showTypingIndicator(true);

        chatService.sendMessage(message, new ChatService.ChatCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    showTypingIndicator(false);
                    addMessageToChat(response, false);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showTypingIndicator(false);
                    addMessageToChat("오류: " + error, false);
                });
            }
        });
    }

    private void addMessageToChat(String message, boolean isUser) {
        TextView textView = new TextView(this);
        textView.setText(message);
        textView.setTextSize(14f);
        textView.setPadding(24, 16, 24, 16);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 8, 8, 8);

        if (isUser) {
            params.gravity = Gravity.END;
            textView.setBackgroundResource(R.drawable.bg_chat_user);
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            params.gravity = Gravity.START;
            textView.setBackgroundResource(R.drawable.bg_chat_bot);
            textView.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }

        textView.setLayoutParams(params);
        binding.chatMessagesContainer.addView(textView);

        // Scroll to the bottom
        binding.chatScrollView.post(() -> binding.chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void showTypingIndicator(boolean show) {
        View typingIndicator = binding.chatMessagesContainer.findViewWithTag("typing");
        if (show) {
            if (typingIndicator == null) {
                TextView textView = new TextView(this);
                textView.setTag("typing");
                textView.setText("입력 중...");
                textView.setTextSize(14f);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                params.setMargins(8, 8, 8, 8);
                textView.setLayoutParams(params);
                binding.chatMessagesContainer.addView(textView);
            }
        } else {
            if (typingIndicator != null) {
                binding.chatMessagesContainer.removeView(typingIndicator);
            }
        }
    }

    private void checkAndShowEventForFeed(Runnable afterEvent) {
        if (eventManager.shouldTriggerEventOnFeed()) {
            RandomEvent event = eventManager.getRandomEvent();

            if (event != null) {
                RandomEventDialog dialog = new RandomEventDialog(this, event,
                        (choiceIndex, choice) -> {
                            handleEventChoice(event, choiceIndex, choice);
                            eventManager.recordEventShown(event.getId());

                            if (afterEvent != null) {
                                afterEvent.run();
                            }
                        });
                dialog.show();
                return;
            }
        }

        if (afterEvent != null) {
            afterEvent.run();
        }
    }

    private void handleEventChoice(RandomEvent event, int choiceIndex, RandomEvent.EventChoice choice) {
        saveEvolutionStat(choice.getStatType(), choice.getStatValue());

        String message = "📝 " + choice.getDescription();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void saveEvolutionStat(String statType, int value) {
        SharedPreferences prefs = getSharedPreferences("evolution_stats", MODE_PRIVATE);
        int currentValue = prefs.getInt(statType, 0);
        prefs.edit()
                .putInt(statType, currentValue + value)
                .apply();
    }

    @Override
    protected void onDestroy() {
        if (eggAnimator != null) {
            eggAnimator.cancel();
        }
        super.onDestroy();
    }
}
