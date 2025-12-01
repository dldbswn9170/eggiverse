package com.eggiverse.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eggiverse.app.data.ShopData;
import com.eggiverse.app.data.ShopItem;
import com.eggiverse.app.data.db.entity.GameState;
import com.eggiverse.app.databinding.ActivityShopBinding;
import com.eggiverse.app.event.RandomEvent;
import com.eggiverse.app.event.RandomEventDialog;
import com.eggiverse.app.event.RandomEventManager;
import com.eggiverse.app.shop.ShopAdapter;
import com.eggiverse.app.viewmodel.GameViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ShopActivity extends AppCompatActivity {

    private ActivityShopBinding binding;
    private GameViewModel viewModel;
    private ShopAdapter adapter;
    private List<ShopItem> allItems;
    private RandomEventManager eventManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);
        allItems = ShopData.getDefaultItems();
        eventManager = RandomEventManager.getInstance(this);

        setupToolbar();
        setupRecycler();
        setupCategoryTabs();
        observeState();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupRecycler() {
        adapter = new ShopAdapter(filterItemsByCategory(ShopItem.ItemType.FOOD), this::showItemDialog);
        binding.shopRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.shopRecycler.setAdapter(adapter);
    }

    private void setupCategoryTabs() {
        binding.categoryTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ShopItem.ItemType selectedType;
                switch (tab.getPosition()) {
                    case 0:
                        selectedType = ShopItem.ItemType.FOOD;
                        break;
                    case 1:
                        selectedType = ShopItem.ItemType.TOY;
                        break;
                    case 2:
                        selectedType = ShopItem.ItemType.DECORATION;
                        break;
                    default:
                        selectedType = ShopItem.ItemType.FOOD;
                }
                updateItemList(selectedType);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void updateItemList(ShopItem.ItemType type) {
        List<ShopItem> filteredItems = filterItemsByCategory(type);
        adapter.updateItems(filteredItems);
    }

    private List<ShopItem> filterItemsByCategory(ShopItem.ItemType type) {
        List<ShopItem> filtered = new ArrayList<>();
        for (ShopItem item : allItems) {
            if (item.getType() == type) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private void observeState() {
        viewModel.getState().observe(this, state -> {
            if (state == null) return;
            binding.coinChip.setText(String.valueOf(state.getCoin()));
            adapter.setOwnedItems(state.getOwnedItems());
        });
    }

    private void showItemDialog(ShopItem item) {
        GameState state = viewModel.getState().getValue();
        boolean owned = state != null && state.getOwnedItems().contains(item.getId());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_shop_item, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        ImageView icon = dialogView.findViewById(R.id.dialogItemIcon);
        TextView title = dialogView.findViewById(R.id.dialogItemTitle);
        TextView desc = dialogView.findViewById(R.id.dialogItemDesc);
        TextView price = dialogView.findViewById(R.id.dialogItemPrice);
        TextView ownedBadge = dialogView.findViewById(R.id.ownedBadge);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        com.google.android.material.button.MaterialButton btnBuy = dialogView.findViewById(R.id.btnBuy);

        title.setText(item.getName());
        desc.setText(item.getDescription());
        price.setText(String.valueOf(item.getPrice()));

        icon.setImageResource(getIconResourceForDialog(item.getId()));

        if (owned) {
            ownedBadge.setVisibility(View.VISIBLE);
        } else {
            ownedBadge.setVisibility(View.GONE);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnBuy.setOnClickListener(v -> {
            boolean success = viewModel.buyItem(item);

            if (success) {
                checkAndShowEventForShop(() -> {
                    Snackbar.make(binding.getRoot(),
                            item.getName() + "을(를) 구매했습니다!",
                            Snackbar.LENGTH_SHORT).show();
                });
                dialog.dismiss();
            } else {
                Snackbar.make(binding.getRoot(),
                        "코인이 부족합니다!",
                        Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private int getIconResourceForDialog(String itemId) {
        switch (itemId) {
            case "food_star_berry": return R.drawable.food_starfruit;
            case "food_dream_powder": return R.drawable.food_dreamsugar;
            case "food_galaxy_jelly": return R.drawable.food_jelly;
            case "food_moon_cake": return R.drawable.food_bunnycookie;

            case "toy_star_rattle": return R.drawable.toy_starbell;
            case "toy_soft_star": return R.drawable.toy_startoy;
            case "toy_glow_ball": return R.drawable.toy_ball;
            case "toy_jumping_jelly": return R.drawable.toy_jelly;

            case "deco_cloud_bed": return R.drawable.room_cloudbed;
            case "deco_soft_bed": return R.drawable.room_bed;
            case "deco_aurora_lamp": return R.drawable.room_auroralight;
            case "deco_flower_point": return R.drawable.room_flowerpoint;
            case "deco_mini_bookshelf": return R.drawable.room_minibook;
            case "deco_glass_bookshelf": return R.drawable.room_glassbook;
            case "deco_egg_frame": return R.drawable.room_picture;
            case "deco_mini_table": return R.drawable.room_minitable;
            case "deco_star_rug": return R.drawable.room_rug;
            case "deco_moon_light": return R.drawable.room_moonlight;
            case "deco_earth_mobile": return R.drawable.room_earth;
            case "deco_silk_curtain": return R.drawable.room_silkcurtain;

            default: return android.R.drawable.ic_menu_gallery;
        }
    }

    private void checkAndShowEventForShop(Runnable afterEvent) {
        if (eventManager.shouldTriggerEventOnShopBuy()) {
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
}