package com.eggiverse.app.games;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.eggiverse.app.R;
import com.eggiverse.app.databinding.ActivitySlidingPuzzleBinding;
import com.eggiverse.app.viewmodel.GameViewModel;

import java.util.Random;

public class SlidingPuzzleActivity extends AppCompatActivity {

    private ActivitySlidingPuzzleBinding binding;
    private GameViewModel viewModel;
    private final int[] tiles = new int[9];
    private final Random random = new Random();
    private int moves = 0;
    private boolean playing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySlidingPuzzleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GameViewModel.class);

        setSupportActionBar(binding.puzzleToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.puzzleToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
        binding.puzzleStartButton.setOnClickListener(v -> shufflePuzzle());

        initPuzzle();
        renderGrid();
    }

    private void initPuzzle() {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = i;
        }
        moves = 0;
        playing = false;
        updateMoveCounter();
    }

    private void shufflePuzzle() {
        initPuzzle();
        playing = true;
        for (int i = 0; i < 100; i++) {
            int emptyIndex = findEmpty();
            int[] valid = validMoves(emptyIndex);
            int target = valid[random.nextInt(valid.length)];
            swap(emptyIndex, target);
        }
        renderGrid();
    }

    private int findEmpty() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == 0) return i;
        }
        return 0;
    }

    private int[] validMoves(int empty) {
        int row = empty / 3;
        int col = empty % 3;
        int[] tmp = new int[4];
        int count = 0;
        if (row > 0) tmp[count++] = empty - 3;
        if (row < 2) tmp[count++] = empty + 3;
        if (col > 0) tmp[count++] = empty - 1;
        if (col < 2) tmp[count++] = empty + 1;
        int[] result = new int[count];
        System.arraycopy(tmp, 0, result, 0, count);
        return result;
    }

    private void swap(int a, int b) {
        int temp = tiles[a];
        tiles[a] = tiles[b];
        tiles[b] = temp;
    }

    private void renderGrid() {
        GridLayout grid = binding.puzzleGrid;
        grid.removeAllViews();
        for (int i = 0; i < tiles.length; i++) {
            Button button = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.rowSpec = GridLayout.spec(i / 3, 1f);
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            if (tiles[i] == 0) {
                button.setText("");
                button.setEnabled(false);
                button.setBackgroundColor(0x33FFFFFF);
            } else {
                button.setText(String.valueOf(tiles[i]));
                button.setBackgroundColor(getColor(R.color.blue_accent));
                button.setTextColor(getColor(android.R.color.white));
                final int index = i;
                button.setOnClickListener(v -> onTileTap(index));
            }
            grid.addView(button);
        }
    }

    private void onTileTap(int index) {
        if (!playing) return;
        int emptyIndex = findEmpty();
        int[] valid = validMoves(emptyIndex);
        for (int move : valid) {
            if (move == index) {
                swap(emptyIndex, index);
                moves++;
                updateMoveCounter();
                renderGrid();
                if (isSolved()) {
                    onCompleted();
                }
                break;
            }
        }
    }

    private void updateMoveCounter() {
        binding.moveCounter.setText("이동: " + moves);
    }

    private boolean isSolved() {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] != i) return false;
        }
        return true;
    }

    private void onCompleted() {
        playing = false;
        int score = Math.max(0, 500 - moves * 10);
        viewModel.completeMiniGame(score);

        new AlertDialog.Builder(this, R.style.ThemeOverlay_Eggiverse_Dialog)
                .setTitle("퍼즐 완성!")
                .setMessage("이동 횟수: " + moves + "\n점수: " + score)
                .setPositiveButton("다시하기", (d, which) -> shufflePuzzle())
                .setNegativeButton("닫기", (d, which) -> finish())
                .setCancelable(false)
                .show();
    }
}

