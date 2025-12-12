package com.eggiverse.app;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eggiverse.app.adapter.InventoryAdapter;
import com.eggiverse.app.chat.ChatService;
import com.eggiverse.app.data.GameRepository;
import com.eggiverse.app.data.ShopData;
import com.eggiverse.app.data.ShopItem;
import com.eggiverse.app.data.db.entity.GameState;
import com.eggiverse.app.databinding.ActivityMainBinding;
import com.eggiverse.app.event.RandomEvent;
import com.eggiverse.app.event.RandomEventDialog;
import com.eggiverse.app.event.RandomEventManager;
import com.eggiverse.app.evolution.EvolutionDialogManager;
import com.eggiverse.app.evolution.EvolutionManager;
import com.eggiverse.app.evolution.EvolutionType;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ActivityMainBinding binding;
    private GameViewModel viewModel;
    private ObjectAnimator eggAnimator;
    private RandomEventManager eventManager;
    private ChatService chatService;
    private EvolutionManager evolutionManager;
    private EvolutionDialogManager evolutionDialogManager;
    private GameRepository gameRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        eventManager = RandomEventManager.getInstance(this);
        chatService = new ChatService();
        gameRepository = GameRepository.get(); // GameRepository 초기화

        // 진화 시스템 초기화
        EvolutionManager.init(this);
        evolutionManager = EvolutionManager.getInstance();
        evolutionDialogManager = new EvolutionDialogManager(this);

        // 진화 준비 완료 콜백 등록 (포인트 100 도달 시 자동 팝업)
        evolutionManager.setOnEvolutionReadyListener(() -> {
            if (evolutionManager.canEvolve()) {
                Log.d(TAG, "Evolution ready! Showing dialog automatically");
                evolutionDialogManager.showEvolutionChoiceDialog(new EvolutionDialogManager.EvolutionCallback() {
                    @Override
                    public void onEvolutionComplete(EvolutionType selectedType) {
                        Log.d(TAG, "Evolution completed with type: " + selectedType);
                        MainActivity.this.onEvolutionComplete(selectedType);
                    }

                    @Override
                    public void onEvolutionCanceled() {
                        Log.d(TAG, "Evolution canceled");
                        Toast.makeText(MainActivity.this, "진화 취소됨", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        checkAndShowEggNameDialog();
        setupStaticUi();
        observeGameState();
        setupListeners();

        gameRepository.setCoins(99999);
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

    private void checkAndShowEggNameDialog() {
        SharedPreferences prefs = getSharedPreferences("egg_info", MODE_PRIVATE);
        String savedName = prefs.getString("egg_name", null);

        if (savedName == null) {
            showEggNameDialog();
        }
    }

    private void showEggNameDialog() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        LayoutInflater inflater = LayoutInflater.from(this);
        View customView = inflater.inflate(R.layout.dialog_egg_name, null);

        EditText nameInput = customView.findViewById(R.id.eggNameInput);
        Button confirmButton = customView.findViewById(R.id.confirmButton);
        Button cancelButton = customView.findViewById(R.id.cancelButton);

        nameInput.setText("알");
        nameInput.selectAll();

        confirmButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            if (!name.isEmpty()) {
                SharedPreferences prefs = getSharedPreferences("egg_info", MODE_PRIVATE);
                prefs.edit().putString("egg_name", name).apply();

                // EvolutionManager에도 사용자 이름 저장
                evolutionManager.getState().setUserProvidedName(name);
                evolutionManager.saveState();

                dialog.dismiss();
            } else {
                Toast.makeText(MainActivity.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            }
        });

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.setContentView(customView);
        dialog.setCancelable(false);
        dialog.show();
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

        // 진화 시스템이 활성화되면 진화 타입에 따른 이미지 사용
        if (evolutionManager != null && level >= 2) {
            EvolutionType currentType = evolutionManager.getState().getCurrentType();
            drawableId = getCharacterDrawableByLevelAndType(level, currentType);
        } else {
            // 진화 전 기본 이미지
            if (level >= 3) {
                drawableId = R.drawable.level2_1;
            } else if (level >= 2) {
                drawableId = R.drawable.level1;
            } else {
                drawableId = R.drawable.pixel_egg;
            }
        }
        binding.eggImage.setImageResource(drawableId);
    }

    /**
     * 레벨과 진화 타입에 따른 캐릭터 drawable ID 반환
     */
    private int getCharacterDrawableByLevelAndType(int level, EvolutionType type) {
        int typeIndex = type.ordinal() + 1; // TYPE_1 -> 1, TYPE_2 -> 2, TYPE_3 -> 3
        String resourceName = String.format("level%d_%d", level, typeIndex);

        int resId = getResources().getIdentifier(
                resourceName,
                "drawable",
                getPackageName()
        );

        // 리소스가 없으면 기본 이미지 반환
        if (resId == 0) {
            return level >= 3 ? R.drawable.level2_1 : R.drawable.level1;
        }
        return resId;
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called - checking evolution condition");

        // 게임에서 돌아왔을 때 진화 조건 확인
        int currentExp = evolutionManager.getState().getEvolutionExp();
        int lastShownExp = evolutionManager.getState().getLastShownExpForPopup();

        if (evolutionManager.canEvolve() && currentExp >= 100 && currentExp != lastShownExp) {
            Log.d(TAG, "Evolution condition met on resume - showing dialog. EXP: " + currentExp);

            // 이 경험치로 팝업을 보였다고 기록
            evolutionManager.getState().setLastShownExpForPopup(currentExp);
            evolutionManager.saveState();

            evolutionDialogManager.showEvolutionChoiceDialog(new EvolutionDialogManager.EvolutionCallback() {
                @Override
                public void onEvolutionComplete(EvolutionType selectedType) {
                    Log.d(TAG, "Evolution completed with type: " + selectedType);
                    MainActivity.this.onEvolutionComplete(selectedType);
                }

                @Override
                public void onEvolutionCanceled() {
                    Log.d(TAG, "Evolution canceled");
                    Toast.makeText(MainActivity.this, "진화 취소됨", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupListeners() {
        // 먹이 주기 버튼 - 모달 표시
        binding.feedButton.setOnClickListener(v -> showFoodModal());

        // 놀아 주기 버튼 - 모달 표시
        binding.playButton.setOnClickListener(v -> showToyModal());

        // 먹이 모달 닫기
        binding.closeFoodModalButton.setOnClickListener(v ->
                binding.foodModalOverlay.setVisibility(View.GONE));

        // 장난감 모달 닫기
        binding.closeToyModalButton.setOnClickListener(v ->
                binding.toyModalOverlay.setVisibility(View.GONE));

        // 오버레이 클릭시 닫기
        binding.foodModalOverlay.setOnClickListener(v ->
                binding.foodModalOverlay.setVisibility(View.GONE));

        binding.toyModalOverlay.setOnClickListener(v ->
                binding.toyModalOverlay.setVisibility(View.GONE));

        binding.eggImage.setOnClickListener(v -> {
            viewModel.playWithEgg(5);
            Snackbar.make(binding.getRoot(), "알이 좋아합니다! 행복도 +5", Snackbar.LENGTH_SHORT).show();
        });

        // 길게 누르면 진화 팝업 표시
        binding.eggImage.setOnLongClickListener(v -> {
            Log.d(TAG, "Egg long press detected");
            if (evolutionManager.canEvolve()) {
                Log.d(TAG, "canEvolve() returned true, showing evolution dialog");
                evolutionDialogManager.showEvolutionChoiceDialog(new EvolutionDialogManager.EvolutionCallback() {
                    @Override
                    public void onEvolutionComplete(EvolutionType selectedType) {
                        Log.d(TAG, "Evolution completed with type: " + selectedType);
                        MainActivity.this.onEvolutionComplete(selectedType);
                    }

                    @Override
                    public void onEvolutionCanceled() {
                        Log.d(TAG, "Evolution canceled");
                        Toast.makeText(MainActivity.this, "진화 취소됨", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.d(TAG, "canEvolve() returned false");
                Toast.makeText(MainActivity.this, "진화는 레벨 2부터 가능합니다", Toast.LENGTH_SHORT).show();
            }
            return true;
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

    /**
     * 먹이 선택 모달 표시
     */
    private void showFoodModal() {
        // DB에서 보유한 먹이 아이템 가져오기
        List<InventoryAdapter.InventoryItemData> foodItems = getUserFoodInventory();

        if (foodItems.isEmpty()) {
            // 먹이가 없으면 안내 텍스트 표시
            binding.foodRecyclerView.setVisibility(View.GONE);
            binding.emptyFoodText.setVisibility(View.VISIBLE);
        } else {
            // 먹이가 있으면 RecyclerView에 표시
            binding.foodRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyFoodText.setVisibility(View.GONE);

            InventoryAdapter adapter = new InventoryAdapter(this, foodItems, itemData -> {
                // 아이템 클릭시 먹이 사용
                useFoodItem(itemData);
                binding.foodModalOverlay.setVisibility(View.GONE);
            });

            binding.foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.foodRecyclerView.setAdapter(adapter);
        }

        binding.foodModalOverlay.setVisibility(View.VISIBLE);
    }

    /**
     * 장난감 선택 모달 표시
     */
    private void showToyModal() {
        // DB에서 보유한 장난감 아이템 가져오기
        List<InventoryAdapter.InventoryItemData> toyItems = getUserToyInventory();

        if (toyItems.isEmpty()) {
            binding.toyRecyclerView.setVisibility(View.GONE);
            binding.emptyToyText.setVisibility(View.VISIBLE);
        } else {
            binding.toyRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyToyText.setVisibility(View.GONE);

            InventoryAdapter adapter = new InventoryAdapter(this, toyItems, itemData -> {
                // 아이템 클릭시 장난감 사용
                useToyItem(itemData);
                binding.toyModalOverlay.setVisibility(View.GONE);
            });

            binding.toyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            binding.toyRecyclerView.setAdapter(adapter);
        }

        binding.toyModalOverlay.setVisibility(View.VISIBLE);
    }

    /**
     * DB에서 사용자의 먹이 인벤토리 가져오기
     */
    private List<InventoryAdapter.InventoryItemData> getUserFoodInventory() {
        List<InventoryAdapter.InventoryItemData> inventory = new ArrayList<>();

        // GameState에서 보유 아이템 가져오기
        GameState currentState = gameRepository.getGameState().getValue();
        if (currentState == null) {
            return inventory;
        }

        Set<String> ownedItems = currentState.getOwnedItems();

        // 아이템 ID별 수량 카운트
        Map<String, Integer> itemCounts = new HashMap<>();
        for (String itemId : ownedItems) {
            itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
        }

        // FOOD 타입만 필터링
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            ShopItem item = ShopData.findById(entry.getKey());
            if (item != null && item.getType() == ShopItem.ItemType.FOOD) {
                inventory.add(new InventoryAdapter.InventoryItemData(item, entry.getValue()));
            }
        }

        return inventory;
    }

    /**
     * DB에서 사용자의 장난감 인벤토리 가져오기
     */
    private List<InventoryAdapter.InventoryItemData> getUserToyInventory() {
        List<InventoryAdapter.InventoryItemData> inventory = new ArrayList<>();

        // GameState에서 보유 아이템 가져오기
        GameState currentState = gameRepository.getGameState().getValue();
        if (currentState == null) {
            return inventory;
        }

        Set<String> ownedItems = currentState.getOwnedItems();

        // 아이템 ID별 수량 카운트
        Map<String, Integer> itemCounts = new HashMap<>();
        for (String itemId : ownedItems) {
            itemCounts.put(itemId, itemCounts.getOrDefault(itemId, 0) + 1);
        }

        // TOY 타입만 필터링
        for (Map.Entry<String, Integer> entry : itemCounts.entrySet()) {
            ShopItem item = ShopData.findById(entry.getKey());
            if (item != null && item.getType() == ShopItem.ItemType.TOY) {
                inventory.add(new InventoryAdapter.InventoryItemData(item, entry.getValue()));
            }
        }

        return inventory;
    }

    /**
     * 먹이 아이템 사용
     */
    private void useFoodItem(InventoryAdapter.InventoryItemData itemData) {
        // 이벤트 확인 후 먹이 주기
        checkAndShowEventForFeed(() -> {
            // GameRepository를 통해 아이템 사용
            gameRepository.useItem(itemData.item);

            Snackbar.make(binding.getRoot(),
                    itemData.item.getName() + "을(를) 주었습니다! 배고픔 +" + itemData.item.getEffectValue(),
                    Snackbar.LENGTH_SHORT).show();
        });
    }

    /**
     * 장난감 아이템 사용
     */
    private void useToyItem(InventoryAdapter.InventoryItemData itemData) {
        // GameRepository를 통해 아이템 사용
        gameRepository.useItem(itemData.item);

        Snackbar.make(binding.getRoot(),
                itemData.item.getName() + "(으)로 놀아줬습니다! 행복도 +" + itemData.item.getEffectValue(),
                Snackbar.LENGTH_SHORT).show();
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

        // 진화 시스템에 경험치 추가 (100 도달 시 진화 팝업 자동 표시)
        evolutionManager.addEvolutionExp(choice.getStatValue());

        // 진화 타입별 포인트 추가 (TYPE_2, TYPE_3 선택 가능 여부 결정)
        evolutionManager.addEvolutionPoints(choice.getStatType(), choice.getStatValue());

        // 디버그 로그
        Log.d(TAG, "handleEventChoice - StatType: " + choice.getStatType() + ", Value: " + choice.getStatValue());
        Log.d(TAG, "Evolution Points - TYPE_1: " + evolutionManager.getState().getType1Points() +
                   ", TYPE_2: " + evolutionManager.getState().getType2Points() +
                   ", TYPE_3: " + evolutionManager.getState().getType3Points());

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

    /**
     * 진화 완료 콜백 처리
     * 진화 다이얼로그에서 진화 완료 시 호출
     */
    public void onEvolutionComplete(EvolutionType selectedType) {
        // 캐릭터 이미지 업데이트
        updateEggDrawable(evolutionManager.getState().getCurrentLevel());

        Toast.makeText(this, selectedType.getDisplayName() + "로 진화 완료! 🎉", Toast.LENGTH_SHORT).show();
    }

    /**
     * 진화 흐름:
     * 1. 게임 진행 → 경험치 100 획득 → EvolutionManager.addEvolutionExp() 호출
     * 2. 경험치 100 도달 시 자동으로 진화 팝업 표시 (또는 onResume에서 재확인)
     * 3. 진화 완료 → 경험치 초기화 (다음 진화를 위해 다시 100 필요)
     * 4. 랜덤이벤트에서 포인트 선택 → TYPE_2, TYPE_3 진화 가능 여부 결정
     *
     * 길게 누르기: 진화 가능 상태면 언제든지 진화 팝업 표시
     */

    @Override
    protected void onDestroy() {
        if (eggAnimator != null) {
            eggAnimator.cancel();
        }
        super.onDestroy();
    }

}