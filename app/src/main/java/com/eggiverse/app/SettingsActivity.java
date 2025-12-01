package com.eggiverse.app;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.eggiverse.app.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.settingsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.settingsToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );

        prefs = getSharedPreferences("settings", MODE_PRIVATE);

        setupListeners();
        loadSettings();
        displayEggName();
    }

    private void displayEggName() {
        SharedPreferences eggPrefs = getSharedPreferences("egg_info", MODE_PRIVATE);
        String eggName = eggPrefs.getString("egg_name", "알");
        binding.eggNameDisplay.setText(eggName);
    }

    private void setupListeners() {
        binding.changeNameButton.setOnClickListener(v -> showChangeNameDialog());
        binding.soundToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("sound_enabled", isChecked).apply()
        );
        binding.hungerNotificationToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("hunger_notification", isChecked).apply()
        );
        binding.eventNotificationToggle.setOnCheckedChangeListener((buttonView, isChecked) ->
                prefs.edit().putBoolean("event_notification", isChecked).apply()
        );
    }

    private void loadSettings() {
        binding.soundToggle.setChecked(prefs.getBoolean("sound_enabled", true));
        binding.hungerNotificationToggle.setChecked(prefs.getBoolean("hunger_notification", true));
        binding.eventNotificationToggle.setChecked(prefs.getBoolean("event_notification", true));
    }

    private void showChangeNameDialog() {
        SharedPreferences eggPrefs = getSharedPreferences("egg_info", MODE_PRIVATE);
        String currentName = eggPrefs.getString("egg_name", "알");

        EditText nameInput = new EditText(this);
        nameInput.setHint("새로운 알의 이름");
        nameInput.setText(currentName);
        nameInput.selectAll();

        LinearLayout container = new LinearLayout(this);
        container.setPadding(50, 20, 50, 20);
        container.addView(nameInput);

        new AlertDialog.Builder(this)
                .setTitle("알의 이름 변경")
                .setMessage("새로운 이름을 입력해주세요")
                .setView(container)
                .setPositiveButton("변경", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        eggPrefs.edit().putString("egg_name", newName).apply();
                        binding.eggNameDisplay.setText(newName);
                    }
                })
                .setNegativeButton("취소", null)
                .show();
    }
}

