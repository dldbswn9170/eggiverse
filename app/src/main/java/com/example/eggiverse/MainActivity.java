package com.example.eggiverse; // 본인의 패키지명으로 수정하세요

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. 레이아웃에서 BottomNavigationView를 찾습니다.
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 2. 네비게이션 아이템 선택 리스너를 설정합니다.
        //    이 부분이 ActionBar가 아닌 BottomNavigationView의 클릭을 처리합니다.
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_minigame) {
                    selectedFragment = new MinigameFragment();
                } else if (itemId == R.id.navigation_myroom) {
                    selectedFragment = new MyroomFragment();
                } else if (itemId == R.id.navigation_store) {
                    selectedFragment = new StoreFragment();
                } else if (itemId == R.id.navigation_settings) {
                    selectedFragment = new SettingsFragment();
                }

                if (selectedFragment != null) {
                    // 선택된 Fragment를 화면의 fragment_container에 표시합니다.
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                }
                return true;
            }
        });

        // 3. 앱 시작 시 기본으로 표시될 Fragment를 설정합니다.
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home); // 기본 선택 아이템을 '홈'으로 지정
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }
    }

    // 중요: 아래와 같은 ActionBar용 메뉴 설정 코드가 있다면 제거해야 합니다.
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }
    */
}
