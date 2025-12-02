package com.eggiverse.app;

import android.app.Dialog;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eggiverse.app.data.db.entity.GameState;
import com.eggiverse.app.data.ShopData;
import com.eggiverse.app.data.ShopItem;
import com.eggiverse.app.databinding.ActivityMyRoomBinding;
import com.eggiverse.app.myroom.FurnitureAdapter;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MyRoomActivity extends AppCompatActivity {

    private ActivityMyRoomBinding binding;
    private GameViewModel viewModel;
    private FurnitureAdapter furnitureAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setupToolbar();
        observeDecorations();
        setupEditButton();
        startEggAnimation();
    }

    /**
     * 알 GIF 애니메이션 시작
     */
    private void startEggAnimation() {
        Drawable drawable = binding.eggImageRoom.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.roomToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.roomToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupEditButton() {
        binding.editRoomButton.setOnClickListener(v -> showFurnitureBottomSheet());
    }

    private void observeDecorations() {
        viewModel.getState().observe(this, this::renderDecorations);
    }

    /**
     * 특정 조합(구름침대+달무드등+실크커튼)일 때만 배경 이미지 변경
     * 나머지는 원래대로 텍스트로 표시
     */
    private void renderDecorations(GameState state) {
        if (state == null) return;

        Set<String> decorations = state.getDecorations();

        // 빈 경우
        if (decorations.isEmpty()) {
            binding.roomBackground.setImageResource(R.drawable.bg_myroom);
            binding.emptyDecorations.setVisibility(View.VISIBLE);
            binding.decoContainer.removeAllViews();
            return;
        }

        binding.emptyDecorations.setVisibility(View.GONE);

        // 특정 조합 체크: 구름침대 + 달 무드등 + 실크 커튼
        boolean isSpecialCombo = decorations.size() == 3 &&
                decorations.contains("deco_cloud_bed") &&
                decorations.contains("deco_moon_light") &&
                decorations.contains("deco_silk_curtain");

        if (isSpecialCombo) {
            // 특별 조합 → 배경 이미지 변경, 텍스트 안 보이게
            binding.roomBackground.setImageResource(R.drawable.myroom_cloudbed_moonlight_silkcurtain);
            binding.decoContainer.removeAllViews();
        } else {
            // 일반 조합 → 기본 배경 + 텍스트로 표시
            binding.roomBackground.setImageResource(R.drawable.bg_myroom);
            showDecorationsAsText(decorations);
        }
    }

    /**
     * 원래 방식: 텍스트로 가구 이름 표시
     */
    private void showDecorationsAsText(Set<String> decorations) {
        binding.decoContainer.removeAllViews();

        LinearLayout wrap = new LinearLayout(this);
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setGravity(Gravity.CENTER);

        for (String id : decorations) {
            ShopItem item = ShopData.findById(id);
            if (item == null) continue;

            TextView label = new TextView(this);
            label.setText(item.getName());
            label.setTextColor(getResources().getColor(android.R.color.white, null));
            label.setPadding(24, 16, 24, 16);
            label.setBackgroundResource(R.drawable.bg_owned_badge);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = 16;
            label.setLayoutParams(params);
            wrap.addView(label);
        }

        binding.decoContainer.addView(wrap);
    }

    private void showFurnitureBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.TransparentBottomSheetDialog);

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_furniture, null);
        dialog.setContentView(bottomSheetView);

        // BottomSheetDialog 스타일 설정
        dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        dialog.getBehavior().setSkipCollapsed(true);
        dialog.getBehavior().setDraggable(true);

        // 높이 설정 (70%)
        bottomSheetView.post(() -> {
            View parent = (View) bottomSheetView.getParent();
            ViewGroup.LayoutParams params = parent.getLayoutParams();
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);
            parent.setLayoutParams(params);
        });

        // RecyclerView 설정 - GridLayout (3열)
        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.furnitureRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

        // 보유한 가구 목록 가져오기
        GameState state = viewModel.getState().getValue();
        List<ShopItem> ownedFurniture = new ArrayList<>();
        if (state != null) {
            for (String id : state.getOwnedItems()) {
                ShopItem item = ShopData.findById(id);
                if (item != null && item.getType() == ShopItem.ItemType.DECORATION) {
                    ownedFurniture.add(item);
                }
            }
        }

        // 어댑터 설정
        furnitureAdapter = new FurnitureAdapter(ownedFurniture);

        // 현재 적용된 가구 선택 상태 복원
        if (state != null) {
            furnitureAdapter.setSelectedItems(state.getDecorations());
        }

        recyclerView.setAdapter(furnitureAdapter);

        // 적용하기 버튼
        MaterialButton applyButton = bottomSheetView.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> {
            Set<String> selectedIds = furnitureAdapter.getSelectedIds();
            dialog.dismiss();
            showLoadingDialog(selectedIds);
        });

        dialog.show();
    }

    /**
     * 로딩 다이얼로그 표시
     */
    private void showLoadingDialog(Set<String> selectedIds) {
        android.app.Dialog loadingDialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // 텍스트 반짝임 애니메이션 (코드로 직접 생성)
        TextView loadingText = loadingDialog.findViewById(R.id.loadingText);
        android.view.animation.AlphaAnimation blink = new android.view.animation.AlphaAnimation(1.0f, 0.3f);
        blink.setDuration(800);
        blink.setRepeatMode(android.view.animation.Animation.REVERSE);
        blink.setRepeatCount(android.view.animation.Animation.INFINITE);
        loadingText.startAnimation(blink);

        // 1.5초 후 적용
        binding.getRoot().postDelayed(() -> {
            viewModel.updateDecorations(selectedIds);
            loadingDialog.dismiss();
        }, 1500);
    }
}