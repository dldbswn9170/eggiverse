package com.eggiverse.app.event;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.eggiverse.app.R;
import com.google.android.material.card.MaterialCardView;

public class RandomEventDialog extends Dialog {

    private static final String TAG = "RandomEventDialog";

    public interface OnChoiceSelectedListener {
        void onChoiceSelected(int choiceIndex, RandomEvent.EventChoice choice);
    }

    private final RandomEvent event;
    private final OnChoiceSelectedListener listener;

    public RandomEventDialog(@NonNull Context context, RandomEvent event, OnChoiceSelectedListener listener) {
        super(context);
        this.event = event;
        this.listener = listener;

        setupDialog();
    }

    private void setupDialog() {
        Log.d(TAG, "setupDialog() - 랜덤 이벤트 다이얼로그 설정 시작");

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_random_event, null);
        setContentView(view);

        // 다이얼로그 배경 투명하게
        if (getWindow() != null) {
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Log.d(TAG, "setupDialog() - 이벤트 ID: " + event.getId() + ", 제목: " + event.getTitle());

        // 뷰 바인딩
        TextView titleView = view.findViewById(R.id.eventTitle);
        TextView descView = view.findViewById(R.id.eventDescription);

        MaterialCardView choice1Card = view.findViewById(R.id.choice1Card);
        TextView choice1Text = view.findViewById(R.id.choice1Text);
        TextView choice1Desc = view.findViewById(R.id.choice1Desc);

        MaterialCardView choice2Card = view.findViewById(R.id.choice2Card);
        TextView choice2Text = view.findViewById(R.id.choice2Text);
        TextView choice2Desc = view.findViewById(R.id.choice2Desc);

        MaterialCardView choice3Card = view.findViewById(R.id.choice3Card);
        TextView choice3Text = view.findViewById(R.id.choice3Text);
        TextView choice3Desc = view.findViewById(R.id.choice3Desc);

        // 데이터 설정
        titleView.setText(event.getTitle());
        descView.setText(event.getDescription());

        RandomEvent.EventChoice[] choices = event.getChoices();

        if (choices.length > 0) {
            choice1Text.setText(choices[0].getText());
            choice1Desc.setText(choices[0].getDescription());
            setupChoiceCard(choice1Card, 0, choices[0]);
        }

        if (choices.length > 1) {
            choice2Text.setText(choices[1].getText());
            choice2Desc.setText(choices[1].getDescription());
            setupChoiceCard(choice2Card, 1, choices[1]);
        }

        if (choices.length > 2) {
            choice3Text.setText(choices[2].getText());
            choice3Desc.setText(choices[2].getDescription());
            setupChoiceCard(choice3Card, 2, choices[2]);
        }

        // 다이얼로그 취소 불가 (선택지 중 하나를 반드시 선택해야 함)
        setCancelable(false);
    }

    private void setupChoiceCard(MaterialCardView card, int index, RandomEvent.EventChoice choice) {
        card.setOnClickListener(v -> {
            Log.d(TAG, "setupChoiceCard() - 선택지 클릭됨. Index: " + index + ", StatType: " + choice.getStatType() + ", Value: " + choice.getStatValue());

            // 선택 효과 (테두리 색상 변경)
            card.setStrokeColor(0xFFFFC107); // space_accent 색상

            // 약간의 딜레이 후 콜백 호출 및 다이얼로그 닫기
            card.postDelayed(() -> {
                Log.d(TAG, "setupChoiceCard() - 콜백 호출 전");
                if (listener != null) {
                    Log.d(TAG, "setupChoiceCard() - listener.onChoiceSelected() 호출");
                    listener.onChoiceSelected(index, choice);
                } else {
                    Log.d(TAG, "setupChoiceCard() - listener가 null!");
                }
                dismiss();
            }, 200);
        });
    }
}