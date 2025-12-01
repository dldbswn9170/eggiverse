package com.eggiverse.app.evolution;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;

/**
 * 진화 시스템 관리자
 * - 진화 상태 저장/로드
 * - 진화 조건 판단
 * - 포인트 관리
 */
public class EvolutionManager {
    private static EvolutionManager instance;
    private final SharedPreferences prefs;
    private final Gson gson;
    private EvolutionState state;
    private OnEvolutionReadyListener evolutionReadyListener;

    private static final String PREF_NAME = "evolution_data";
    private static final String KEY_STATE = "evolution_state";
    private static final String TAG = "EvolutionManager";

    // 진화 조건 설정
    private static final int BASE_POINTS_FOR_EVOLUTION = 100;    // 동일 타입 유지에 필요한 포인트
    private static final int PENALTY_MULTIPLIER = 2;              // 다른 타입 변경 시 2배

    /**
     * 진화 준비 완료 콜백 인터페이스
     */
    public interface OnEvolutionReadyListener {
        void onEvolutionReady();
    }

    private EvolutionManager(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        loadState();
    }

    public static void init(Context context) {
        if (instance == null) {
            instance = new EvolutionManager(context);
        }
    }

    public static EvolutionManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("EvolutionManager not initialized");
        }
        return instance;
    }

    /**
     * 진화 상태 로드
     */
    private void loadState() {
        String json = prefs.getString(KEY_STATE, null);
        if (json != null) {
            this.state = gson.fromJson(json, EvolutionState.class);
        } else {
            this.state = new EvolutionState();
            saveState();
        }
    }

    /**
     * 진화 상태 저장
     */
    public void saveState() {
        String json = gson.toJson(state);
        prefs.edit().putString(KEY_STATE, json).apply();
    }

    /**
     * 현재 진화 상태 반환
     */
    public EvolutionState getState() {
        return state;
    }

    /**
     * 콜백 리스너 설정 (경험치 100 도달 시 호출)
     */
    public void setOnEvolutionReadyListener(OnEvolutionReadyListener listener) {
        this.evolutionReadyListener = listener;
    }

    /**
     * 진화 경험치 추가 (100 도달 시 진화 팝업 표시)
     * @param exp 추가할 경험치
     */
    public void addEvolutionExp(int exp) {
        state.addEvolutionExp(exp);
        saveState();

        Log.d(TAG, "addEvolutionExp() - Current EXP: " + state.getEvolutionExp());

        // 경험치 100 도달 시 진화 팝업
        if (state.getEvolutionExp() >= 100 && canEvolve() && evolutionReadyListener != null) {
            Log.d(TAG, "addEvolutionExp() - Evolution ready triggered! EXP reached 100");
            evolutionReadyListener.onEvolutionReady();
        }
    }

    /**
     * 이벤트에서 포인트 추가 (TYPE_2, TYPE_3 선택 가능 여부 결정)
     * @param statType 진화 타입 (예: "adventurer", "scholar")
     * @param value 추가할 포인트
     */
    public void addEvolutionPoints(String statType, int value) {
        EvolutionType type = mapStatTypeToEvolutionType(statType);
        state.addPoints(type, value);
        saveState();

        Log.d(TAG, "addEvolutionPoints() - Type: " + type + ", Points: " + value);
    }

    /**
     * 다음 레벨 진화 가능 여부 판단
     */
    public boolean canEvolve() {
        boolean can = state.getCurrentLevel() < 3; // 레벨 3까지만 진화 가능
        Log.d(TAG, "canEvolve() called: currentLevel=" + state.getCurrentLevel() + ", can=" + can);
        return can;
    }

    /**
     * 특정 타입으로 진화 가능 여부 판단
     * @param targetType 목표 진화 타입
     * @return 진화 가능 여부
     */
    public boolean canEvolveToType(EvolutionType targetType) {
        int requiredPoints;

        if (targetType == state.getCurrentType()) {
            // 동일 타입 유지: BASE_POINTS_FOR_EVOLUTION 필요
            requiredPoints = BASE_POINTS_FOR_EVOLUTION;
        } else {
            // 다른 타입으로 변경: 2배 필요
            requiredPoints = BASE_POINTS_FOR_EVOLUTION * PENALTY_MULTIPLIER;
        }

        return state.getPoints(targetType) >= requiredPoints;
    }

    /**
     * 특정 타입으로 진화 수행
     */
    public boolean evolveToType(EvolutionType targetType) {
        Log.d(TAG, "evolveToType() called with type=" + targetType);

        if (!canEvolveToType(targetType)) {
            Log.d(TAG, "evolveToType() FAILED: cannot evolve to type " + targetType);
            return false;
        }

        // 진화 상태 업데이트
        state.setCurrentLevel(state.getCurrentLevel() + 1);
        state.setCurrentType(targetType);
        state.setCurrentCharacterName(generateCharacterName(state.getCurrentLevel(), targetType));
        // 경험치는 초기화하지 않음 - 계속 누적되어 다음 진화까지 쌓임

        saveState();
        Log.d(TAG, "evolveToType() SUCCESS: new level=" + state.getCurrentLevel() + ", type=" + targetType);
        return true;
    }

    /**
     * 포인트 체크 없이 강제 진화 (기본 진화용)
     * 포인트가 부족할 때 TYPE_1로 강제 진화
     */
    public void forceEvolveToType(EvolutionType targetType) {
        Log.d(TAG, "forceEvolveToType() called with type=" + targetType + " (no point check)");

        // 포인트 체크 없이 강제 진화
        state.setCurrentLevel(state.getCurrentLevel() + 1);
        state.setCurrentType(targetType);
        state.setCurrentCharacterName(generateCharacterName(state.getCurrentLevel(), targetType));
        // 경험치는 초기화하지 않음 - 계속 누적되어 다음 진화까지 쌓임

        saveState();
        Log.d(TAG, "forceEvolveToType() SUCCESS: forced evolution to level=" + state.getCurrentLevel() + ", type=" + targetType);
    }

    /**
     * 랜덤으로 진화 수행 (진화 선택 없이 자동 진화)
     */
    public EvolutionType evolveRandomly() {
        EvolutionType[] types = getAvailableEvolutionTypes();
        EvolutionType randomType = types[(int)(Math.random() * types.length)];

        // 랜덤 타입으로 강제 진화 (포인트 체크 무시)
        state.setCurrentLevel(state.getCurrentLevel() + 1);
        state.setCurrentType(randomType);
        state.setCurrentCharacterName(generateCharacterName(state.getCurrentLevel(), randomType));
        // 경험치는 초기화하지 않음 - 계속 누적되어 다음 진화까지 쌓임

        saveState();
        return randomType;
    }

    /**
     * 다음 레벨에서 가능한 3가지 진화 타입 반환
     */
    public EvolutionType[] getAvailableEvolutionTypes() {
        return new EvolutionType[]{
                EvolutionType.TYPE_1,
                EvolutionType.TYPE_2,
                EvolutionType.TYPE_3
        };
    }

    /**
     * 진화 가능 여부와 필요 포인트 정보
     */
    public EvolutionInfo getEvolutionInfo(EvolutionType targetType) {
        int requiredPoints = (targetType == state.getCurrentType())
                ? BASE_POINTS_FOR_EVOLUTION
                : BASE_POINTS_FOR_EVOLUTION * PENALTY_MULTIPLIER;

        int currentPoints = state.getPoints(targetType);
        boolean canEvolve = currentPoints >= requiredPoints;

        return new EvolutionInfo(targetType, currentPoints, requiredPoints, canEvolve);
    }

    /**
     * SharedPreferences에 저장된 이벤트 포인트를 EvolutionType으로 변환
     */
    private EvolutionType mapStatTypeToEvolutionType(String statType) {
        // 이벤트의 statType을 EvolutionType으로 매핑
        if (statType.contains("scholar") || statType.contains("scientist") || statType.contains("sage")) {
            return EvolutionType.TYPE_2;
        } else if (statType.contains("collector") || statType.contains("artist") || statType.contains("painter")) {
            return EvolutionType.TYPE_3;
        } else {
            return EvolutionType.TYPE_1;
        }
    }

    /**
     * 캐릭터 이름 생성
     */
    private String generateCharacterName(int level, EvolutionType type) {
        String typeName = type.getDisplayName();
        return String.format("Level %d - %s", level, typeName);
    }

    /**
     * 테스트용: 진화 상태 레벨을 직접 설정
     * 사용: evolutionManager.testSetLevel(2)
     */
    public void testSetLevel(int level) {
        if (level < 1 || level > 3) {
            Log.e(TAG, "testSetLevel() ERROR: invalid level " + level + " (must be 1-3)");
            return;
        }
        state.setCurrentLevel(level);
        saveState();
        Log.d(TAG, "testSetLevel() changed level to " + level);
    }

    /**
     * 테스트용: 모든 진화 포인트 초기화
     * 사용: evolutionManager.resetEvolutionPoints()
     */
    public void resetEvolutionPoints() {
        state.setType1Points(0);
        state.setType2Points(0);
        state.setType3Points(0);
        saveState();
        Log.d(TAG, "resetEvolutionPoints() all points reset to 0");
    }

    /**
     * 진화 정보 클래스
     */
    public static class EvolutionInfo {
        public final EvolutionType type;
        public final int currentPoints;
        public final int requiredPoints;
        public final boolean canEvolve;

        public EvolutionInfo(EvolutionType type, int currentPoints, int requiredPoints, boolean canEvolve) {
            this.type = type;
            this.currentPoints = currentPoints;
            this.requiredPoints = requiredPoints;
            this.canEvolve = canEvolve;
        }
    }
}
