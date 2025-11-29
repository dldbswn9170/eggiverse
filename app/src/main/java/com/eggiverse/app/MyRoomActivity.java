package com.eggiverse.app;

import android.app.Dialog;
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

import com.eggiverse.app.data.ShopData;
import com.eggiverse.app.data.ShopItem;
import com.eggiverse.app.data.db.entity.GameState;
import com.eggiverse.app.databinding.ActivityMyRoomBinding;
import com.eggiverse.app.myroom.FurnitureAdapter;
import com.eggiverse.app.viewmodel.GameViewModel;
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

    private void renderDecorations(GameState state) {
        if (state == null) return;

        Set<String> decorations = state.getDecorations();

        if (decorations.isEmpty()) {
            binding.roomBackground.setImageResource(R.drawable.bg_myroom);
            binding.emptyDecorations.setVisibility(View.VISIBLE);
            binding.decoContainer.removeAllViews();
            return;
        }

        binding.emptyDecorations.setVisibility(View.GONE);

        boolean isSpecialCombo = decorations.size() == 3 &&
                decorations.contains("deco_cloud_bed") &&
                decorations.contains("deco_moon_light") &&
                decorations.contains("deco_silk_curtain");

        if (isSpecialCombo) {
            binding.roomBackground.setImageResource(R.drawable.myroom_cloudbed_moonlight_silkcurtain);
            binding.decoContainer.removeAllViews();
        } else {
            binding.roomBackground.setImageResource(R.drawable.bg_myroom);
            showDecorationsAsText(decorations);
        }
    }

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
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_furniture, null);
        dialog.setContentView(bottomSheetView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setDimAmount(0.5f);

            WindowManager.LayoutParams params = window.getAttributes();
            params.height = (int) (getResources().getDisplayMetrics().heightPixels * 0.7);
            window.setAttributes(params);
        }

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.furnitureRecycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);

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

        furnitureAdapter = new FurnitureAdapter(ownedFurniture);

        if (state != null) {
            furnitureAdapter.setSelectedItems(state.getDecorations());
        }

        recyclerView.setAdapter(furnitureAdapter);

        MaterialButton applyButton = bottomSheetView.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(v -> {
            Set<String> selectedIds = furnitureAdapter.getSelectedIds();
            viewModel.updateDecorations(selectedIds);
            dialog.dismiss();
        });

        dialog.show();
    }
}
