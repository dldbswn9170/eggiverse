package com.eggiverse.app;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class IntroActivity extends AppCompatActivity {

    private ImageView eggImageView;
    private TextView storyText;
    private View clickToSkip;
    private int currentStory = 0;

    private String[] stories = {
            "어느 날, 신비로운 우주선에서\n작은 알이 날아왔다...",
            "알은 반짝이는 빛을 내며\n당신의 손 안에 떨어졌다.",
            "이제 당신은 이 우주 알의\n보호자가 되었다!",
            "알을 돌보고 함께 성장하며\n특별한 여정을 시작하세요."
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        eggImageView = findViewById(R.id.introEggImage);
        storyText = findViewById(R.id.storyText);
        clickToSkip = findViewById(R.id.clickToSkip);

        // GIF 애니메이션 시작
        startEggAnimation();

        // 첫 번째 스토리 표시
        showStory(currentStory);

        // 화면 전체 클릭으로 다음 스토리
        findViewById(R.id.introRoot).setOnClickListener(v -> nextStory());
    }

    private void startEggAnimation() {
        Drawable drawable = eggImageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void showStory(int index) {
        if (index >= stories.length) {
            // 스토리 끝 → 메인으로 이동
            startMainActivity();
            return;
        }

        storyText.setText(stories[index]);

        // 텍스트 반짝이는 애니메이션
        AlphaAnimation blink = new AlphaAnimation(1.0f, 0.5f);
        blink.setDuration(1000);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        storyText.startAnimation(blink);

        // 마지막 스토리면 "시작하기" 표시
        if (index == stories.length - 1) {
            clickToSkip.setVisibility(View.VISIBLE);
            ((TextView) clickToSkip).setText("탭하여 시작하기");
        }
    }

    private void nextStory() {
        currentStory++;
        showStory(currentStory);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        
        // 페이드 전환 효과
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // 뒤로가기 방지 (인트로는 건너뛸 수만 있음)
        nextStory();
    }
}
