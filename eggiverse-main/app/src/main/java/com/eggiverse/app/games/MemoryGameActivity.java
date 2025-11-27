package com.eggiverse.app.games;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eggiverse.app.R;
import com.eggiverse.app.databinding.ActivityMemoryGameBinding;
import com.eggiverse.app.viewmodel.GameViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryGameActivity extends AppCompatActivity implements MemoryCardAdapter.OnCardClickListener {

    private ActivityMemoryGameBinding binding;
    private GameViewModel viewModel;
    private MemoryCardAdapter adapter;
    private final List<MemoryCardAdapter.CardState> cards = new ArrayList<>();
    private int firstIndex = -1;
    private int secondIndex = -1;
    private boolean canFlip = true;
    private int moves = 0;
    private int matchedPairs = 0;
    private boolean playing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMemoryGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setSupportActionBar(binding.memoryToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.memoryToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
        binding.memoryStartButton.setOnClickListener(v -> startGame());

        adapter = new MemoryCardAdapter(cards, this);
        binding.memoryGrid.setLayoutManager(new GridLayoutManager(this, 4));
        binding.memoryGrid.setAdapter(adapter);
    }

    private void startGame() {
        cards.clear();
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            values.add(i);
            values.add(i);
        }
        Collections.shuffle(values);
        for (int value : values) {
            MemoryCardAdapter.CardState state = new MemoryCardAdapter.CardState();
            state.value = value;
            state.flipped = false;
            state.matched = false;
            cards.add(state);
        }
        firstIndex = -1;
        secondIndex = -1;
        moves = 0;
        matchedPairs = 0;
        canFlip = true;
        playing = true;
        updateStats();
        adapter.notifyDataSetChanged();
    }

    private void updateStats() {
        binding.memoryMoves.setText("시도: " + moves);
        binding.memoryMatched.setText("매칭: " + matchedPairs + "/6");
    }

    @Override
    public void onCardClick(int position) {
        if (!playing || !canFlip) return;
        MemoryCardAdapter.CardState card = cards.get(position);
        if (card.flipped || card.matched) return;

        card.flipped = true;
        adapter.notifyItemChanged(position);

        if (firstIndex == -1) {
            firstIndex = position;
        } else if (secondIndex == -1) {
            secondIndex = position;
            canFlip = false;
            binding.memoryGrid.postDelayed(this::checkMatch, 800);
        }
    }

    private void checkMatch() {
        if (firstIndex == -1 || secondIndex == -1) return;
        MemoryCardAdapter.CardState first = cards.get(firstIndex);
        MemoryCardAdapter.CardState second = cards.get(secondIndex);

        if (first.value == second.value) {
            first.matched = true;
            second.matched = true;
            matchedPairs++;
            if (matchedPairs == 6) {
                onCompleted();
            }
        } else {
            first.flipped = false;
            second.flipped = false;
        }

        firstIndex = -1;
        secondIndex = -1;
        moves++;
        canFlip = true;
        updateStats();
        adapter.notifyDataSetChanged();
    }

    private void onCompleted() {
        playing = false;
        int score = Math.max(100, 500 - moves * 20);
        viewModel.completeMiniGame(score);

        new AlertDialog.Builder(this, R.style.ThemeOverlay_Eggiverse_Dialog)
                .setTitle("게임 완료!")
                .setMessage("시도 횟수: " + moves + "\n점수: " + score)
                .setPositiveButton("다시하기", (d, which) -> startGame())
                .setNegativeButton("닫기", (d, which) -> finish())
                .setCancelable(false)
                .show();
    }
}

