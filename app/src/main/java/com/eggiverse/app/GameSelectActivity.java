package com.eggiverse.app;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.eggiverse.app.databinding.ActivityGameSelectBinding;
import com.eggiverse.app.games.AlienTypingGameActivity;
import com.eggiverse.app.games.DoodleJumpActivity;
import com.eggiverse.app.games.FlappyBirdActivity;
import com.eggiverse.app.games.GameOptionAdapter;
import com.eggiverse.app.games.MemoryGameActivity;
import com.eggiverse.app.games.RhythmGameActivity;
import com.eggiverse.app.games.SlidingPuzzleActivity;
import com.eggiverse.app.games.StarTapGameActivity;

import java.util.Arrays;

public class GameSelectActivity extends AppCompatActivity {

    private ActivityGameSelectBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameSelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.gameToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        binding.gameToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );

        GameOptionAdapter adapter = new GameOptionAdapter(Arrays.asList(
                new GameOptionAdapter.GameOption("별 터치 게임", "30초 안에 별을 많이 터치하세요!", R.drawable.game_startouch, StarTapGameActivity.class),
                new GameOptionAdapter.GameOption("슬라이딩 퍼즐", "숫자를 맞춰보세요!", R.drawable.game_slidingpuzzle, SlidingPuzzleActivity.class),
                new GameOptionAdapter.GameOption("기억력 게임", "같은 그림을 찾으세요!", R.drawable.game_memorygame, MemoryGameActivity.class),
                new GameOptionAdapter.GameOption("두들 점프", "플랫폼을 밟고 최대한 높이 올라가세요!", R.drawable.game_doodle, DoodleJumpActivity.class),
                new GameOptionAdapter.GameOption("플래피 버드", "장애물을 피해서 최대한 멀리 날아가세요!", R.drawable.game_bird, FlappyBirdActivity.class),
                new GameOptionAdapter.GameOption("리듬 게임", "내려오는 노트를 타이밍에 맞춰 터치하세요!", R.drawable.game_rhythm, RhythmGameActivity.class),
                new GameOptionAdapter.GameOption("타이핑 게임", "단어를 입력해 외계인을 물리치세요!", R.drawable.game_typing, AlienTypingGameActivity.class)
        ));

        binding.gameRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.gameRecycler.setAdapter(adapter);
    }
}
