package com.eggiverse.app.evolution;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.os.Handler;
import android.os.Looper;

import com.eggiverse.app.R;
import com.bumptech.glide.Glide;

/**
 * 진화 다이얼로그 관리자
 * - 진화 선택 팝업 표시
 * - 진화 연출 애니메이션 표시
 * - 진화 결과 화면 표시
 */
public class EvolutionDialogManager {
    private static final String TAG = "EvolutionDialogManager";
    private final Context context;
    private final EvolutionManager evolutionManager;
    private Dialog currentDialog;
    private EvolutionCallback callback;

    private static final int GIF_ANIMATION_DURATION = 7000; // 7초

    public interface EvolutionCallback {
        void onEvolutionComplete(EvolutionType selectedType);
        void onEvolutionCanceled();
    }

    public EvolutionDialogManager(Context context) {
        this.context = context;
        this.evolutionManager = EvolutionManager.getInstance();
    }

    /**
     * 진화 선택 다이얼로그 표시
     */
    public void showEvolutionChoiceDialog(EvolutionCallback callback) {
        Log.d(TAG, "showEvolutionChoiceDialog() called");
        this.callback = callback;

        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_evolution_choice, null);

        setupChoiceDialog(dialog, dialogView);
        dialog.setContentView(dialogView);
        dialog.setCancelable(false);

        this.currentDialog = dialog;
        dialog.show();
        Log.d(TAG, "Evolution choice dialog displayed");
    }

    /**
     * 진화 선택 다이얼로그 설정
     */
    private void setupChoiceDialog(Dialog dialog, View dialogView) {
        EvolutionType[] types = evolutionManager.getAvailableEvolutionTypes();

        // 3가지 진화 타입 설정
        setupEvolutionTypeButton(dialogView, R.id.evolution_type_1, types[0], dialog);
        setupEvolutionTypeButton(dialogView, R.id.evolution_type_2, types[1], dialog);
        setupEvolutionTypeButton(dialogView, R.id.evolution_type_3, types[2], dialog);

        // 취소 버튼
        Button cancelButton = dialogView.findViewById(R.id.cancel_evolution_button);
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onEvolutionCanceled();
            }
        });
    }

    /**
     * 각 진화 타입 버튼 설정
     */
    private void setupEvolutionTypeButton(View dialogView, int frameLayoutId, EvolutionType type, Dialog dialog) {
        FrameLayout typeFrame = dialogView.findViewById(frameLayoutId);
        ImageView typeImage = null;
        View overlay = null;
        TextView lockText = null;

        // ID 매핑
        if (frameLayoutId == R.id.evolution_type_1) {
            typeImage = dialogView.findViewById(R.id.image_type_1);
            overlay = dialogView.findViewById(R.id.overlay_type_1);
            lockText = dialogView.findViewById(R.id.lock_text_type_1);
        } else if (frameLayoutId == R.id.evolution_type_2) {
            typeImage = dialogView.findViewById(R.id.image_type_2);
            overlay = dialogView.findViewById(R.id.overlay_type_2);
            lockText = dialogView.findViewById(R.id.lock_text_type_2);
        } else if (frameLayoutId == R.id.evolution_type_3) {
            typeImage = dialogView.findViewById(R.id.image_type_3);
            overlay = dialogView.findViewById(R.id.overlay_type_3);
            lockText = dialogView.findViewById(R.id.lock_text_type_3);
        }

        // 동적으로 현재 레벨에 맞는 이미지 로드
        int currentLevel = evolutionManager.getState().getCurrentLevel();
        int nextLevel = currentLevel + 1;
        int imageResId = getCharacterImageResource(nextLevel, type);
        if (typeImage != null && imageResId != 0) {
            typeImage.setImageResource(imageResId);
        }

        // 진화 가능 여부 판단
        EvolutionManager.EvolutionInfo info = evolutionManager.getEvolutionInfo(type);

        if (info.canEvolve) {
            // 진화 가능 상태
            typeFrame.setOnClickListener(v -> {
                dialog.dismiss();
                showEvolutionAnimation(type);
            });
        } else {
            // 비활성화 상태: 기본 진화(TYPE_1)만 클릭 가능
            if (type == EvolutionType.TYPE_1) {
                // TYPE_1이 부족하면 기본 진화로 강제 진행 (포인트 체크 없음)
                typeFrame.setOnClickListener(v -> {
                    dialog.dismiss();
                    // 기본 진화: TYPE_1로 강제 진화 (포인트 부족 무시)
                    showEvolutionAnimation(EvolutionType.TYPE_1);
                });
                // TYPE_1은 잠금 표시 안 함 (항상 선택 가능)
            } else {
                // TYPE_2, TYPE_3는 잠금
                if (overlay != null) {
                    overlay.setVisibility(View.VISIBLE);
                }
                if (lockText != null) {
                    lockText.setVisibility(View.VISIBLE);
                    lockText.setText(String.format("%d/%d", info.currentPoints, info.requiredPoints));
                }
            }
        }
    }

    /**
     * 진화 애니메이션 표시
     */
    private void showEvolutionAnimation(EvolutionType selectedType) {
        Log.d(TAG, "showEvolutionAnimation() called with type: " + selectedType);
        Dialog animationDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(context);
        View overlayView = inflater.inflate(R.layout.evolution_overlay, null);

        animationDialog.setContentView(overlayView);
        animationDialog.setCancelable(false);
        animationDialog.show();
        Log.d(TAG, "Evolution animation dialog displayed");

        // GIF 애니메이션 시작
        ImageView gifImageView = overlayView.findViewById(R.id.evolution_gif_animation);
        TextView animationText = overlayView.findViewById(R.id.animation_text);

        // 사용자가 입력한 캐릭터 이름을 동적으로 가져옴
        String userProvidedName = evolutionManager.getState().getUserProvidedName();
        if (userProvidedName == null || userProvidedName.isEmpty()) {
            userProvidedName = "알";  // 기본값
        }
        animationText.setText(String.format("...오잉!? %s의 상태가...!", userProvidedName));

        // GIF 애니메이션 실행 (Glide로 GIF 로드)
        Glide.with(context)
                .asGif()
                .load(R.drawable.levelup)
                .into(gifImageView);

        // 7초 후 결과 화면 표시
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showEvolutionResult(overlayView, selectedType, animationDialog);
        }, GIF_ANIMATION_DURATION);
    }

    /**
     * 진화 결과 화면 표시
     */
    private void showEvolutionResult(View overlayView, EvolutionType selectedType, Dialog dialog) {
        LinearLayout gifContainer = overlayView.findViewById(R.id.gif_animation_container);
        LinearLayout resultContainer = overlayView.findViewById(R.id.result_container);

        // GIF 컨테이너 숨김
        gifContainer.setVisibility(View.GONE);

        // 결과 컨테이너 표시
        resultContainer.setVisibility(View.VISIBLE);

        // 선택한 타입으로 진화 수행
        Log.d(TAG, "showEvolutionResult() calling evolveToType with type: " + selectedType);
        boolean evolved = evolutionManager.evolveToType(selectedType);

        // 최종 진화 타입 (포인트 부족 시 TYPE_1로 변경됨)
        EvolutionType finalEvolutionType = selectedType;

        if (!evolved) {
            // 진화 실패 (포인트 부족) → 기본 진화(TYPE_1)로 강제 진행
            Log.d(TAG, "Evolution failed - insufficient points for " + selectedType + ", forcing TYPE_1 evolution");
            evolved = evolutionManager.evolveToType(EvolutionType.TYPE_1);

            if (!evolved) {
                // TYPE_1도 실패할 수 없음 (포인트 체크 없이 강제 진화)
                // 강제로 진화 수행
                evolutionManager.forceEvolveToType(EvolutionType.TYPE_1);
                evolved = true;
                Log.d(TAG, "Forced TYPE_1 evolution");
            }

            Log.d(TAG, "Fallback to TYPE_1 evolution - showing result screen");
            finalEvolutionType = EvolutionType.TYPE_1;
        }

        Log.d(TAG, "Evolution succeeded - showing result screen");

        // 진화된 이미지 설정 (level 3이 되므로 level3 이미지 사용)
        int nextLevel = evolutionManager.getState().getCurrentLevel();
        int imageResId = getCharacterImageResource(nextLevel, finalEvolutionType);

        ImageView evolvedCharacter = overlayView.findViewById(R.id.evolved_character_image);
        evolvedCharacter.setImageResource(imageResId);

        // 진화 완료 후 3개의 캐릭터를 보여주는 이미지 설정
        // level3_1, level3_2, level3_3 이미지를 사용
        displayEvolvedCharacters(overlayView, nextLevel, finalEvolutionType);

        // 축하 메시지 설정
        String characterName = evolutionManager.getState().getUserProvidedName();
        if (characterName == null || characterName.isEmpty()) {
            characterName = "알";  // 기본값
        }
        int currentLevel = evolutionManager.getState().getCurrentLevel();
        TextView resultText = overlayView.findViewById(R.id.result_text);
        resultText.setText(String.format("축하합니다!\n%s이 진화에 성공했습니다!", characterName));

        // 애니메이션 효과 (떠 있는 효과)
        addFloatingAnimation(evolvedCharacter);

        // 확인 버튼
        final EvolutionType resultType = finalEvolutionType;
        Button confirmButton = overlayView.findViewById(R.id.confirm_evolution_button);
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            if (callback != null) {
                callback.onEvolutionComplete(resultType);
            }
        });
    }

    /**
     * 진화 완료 시 3개의 캐릭터를 표시
     * level2->level3 진화 시 3가지 타입 모두 표시
     */
    private void displayEvolvedCharacters(View overlayView, int level, EvolutionType selectedType) {
        // 현재는 선택된 타입의 이미지만 표시
        // 나중에 3개 모두 보여주고 싶다면 아래 주석 해제

        // ImageView를 추가로 생성하여 3개 캐릭터 표시 가능
        // 예: level3_1, level3_2, level3_3 이미지를 가로로 배열
    }

    /**
     * 떠 있는 애니메이션 추가
     */
    private void addFloatingAnimation(ImageView imageView) {
        imageView.animate()
                .translationY(-20f)
                .setDuration(800)
                .withEndAction(() -> {
                    imageView.animate()
                            .translationY(20f)
                            .setDuration(800)
                            .start();
                })
                .start();
    }

    /**
     * 레벨과 타입에 해당하는 캐릭터 이미지 리소스 반환
     */
    private int getCharacterImageResource(int level, EvolutionType type) {
        int typeIndex = type.ordinal() + 1; // TYPE_1 -> 1, TYPE_2 -> 2, TYPE_3 -> 3

        // 리소스 네이밍: level{level}_{typeIndex}
        String resourceName = String.format("level%d_%d", level, typeIndex);

        return context.getResources().getIdentifier(
                resourceName,
                "drawable",
                context.getPackageName()
        );
    }

    /**
     * 진화 다이얼로그 닫기
     */
    public void dismissDialog() {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
    }
}
