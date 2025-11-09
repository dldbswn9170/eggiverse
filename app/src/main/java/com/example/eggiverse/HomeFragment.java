package com.example.eggiverse; // 본인의 패키지명으로 수정하세요

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// R 클래스는 app 모듈의 리소스를 가리키도록 import 합니다.
// import com.example.eggiverse.R;

public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 이 Fragment에 해당하는 레이아웃 파일을 inflate합니다.
        // 예를 들어 R.layout.fragment_home
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
