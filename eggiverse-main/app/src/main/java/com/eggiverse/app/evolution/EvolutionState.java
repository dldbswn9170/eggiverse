package com.eggiverse.app.evolution;

/**
 * 진화 상태 데이터 클래스
 * 각 레벨에서의 진화 타입과 누적 포인트를 관리합니다
 */
public class EvolutionState {
    private int currentLevel;                  // 현재 레벨 (1~3)
    private EvolutionType currentType;         // 현재 진화 타입
    private String currentCharacterName;       // 현재 캐릭터 이름 (사용자가 입력한 이름, 예: "알")
    private String userProvidedName;           // 사용자가 입력한 실제 캐릭터 이름

    private int evolutionExp;                  // 진화 경험치 (100 도달 시 진화 팝업)
    private int type1Points;                   // 타입 1 누적 포인트 (TYPE_2, TYPE_3 선택 가능 여부)
    private int type2Points;                   // 타입 2 누적 포인트
    private int type3Points;                   // 타입 3 누적 포인트

    public EvolutionState() {
        this.currentLevel = 1;
        this.currentType = EvolutionType.TYPE_1;
        this.currentCharacterName = "알";
        this.userProvidedName = "알";
        this.evolutionExp = 0;
        this.type1Points = 0;
        this.type2Points = 0;
        this.type3Points = 0;
    }

    // Getters and Setters
    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public EvolutionType getCurrentType() {
        return currentType;
    }

    public void setCurrentType(EvolutionType currentType) {
        this.currentType = currentType;
    }

    public String getCurrentCharacterName() {
        return currentCharacterName;
    }

    public void setCurrentCharacterName(String currentCharacterName) {
        this.currentCharacterName = currentCharacterName;
    }

    public String getUserProvidedName() {
        return userProvidedName;
    }

    public void setUserProvidedName(String userProvidedName) {
        this.userProvidedName = userProvidedName;
    }

    public int getEvolutionExp() {
        return evolutionExp;
    }

    public void setEvolutionExp(int evolutionExp) {
        this.evolutionExp = evolutionExp;
    }

    public int getType1Points() {
        return type1Points;
    }

    public void setType1Points(int type1Points) {
        this.type1Points = type1Points;
    }

    public int getType2Points() {
        return type2Points;
    }

    public void setType2Points(int type2Points) {
        this.type2Points = type2Points;
    }

    public int getType3Points() {
        return type3Points;
    }

    public void setType3Points(int type3Points) {
        this.type3Points = type3Points;
    }

    /**
     * 진화 경험치 추가 (100 도달 시 진화 팝업)
     */
    public void addEvolutionExp(int exp) {
        this.evolutionExp += exp;
    }

    /**
     * 진화 경험치 초기화 (진화 후)
     */
    public void resetEvolutionExp() {
        this.evolutionExp = 0;
    }

    /**
     * 진화 타입별 포인트 증가
     */
    public void addPoints(EvolutionType type, int points) {
        switch (type) {
            case TYPE_1:
                this.type1Points += points;
                break;
            case TYPE_2:
                this.type2Points += points;
                break;
            case TYPE_3:
                this.type3Points += points;
                break;
        }
    }

    /**
     * 특정 타입의 현재 포인트 조회
     */
    public int getPoints(EvolutionType type) {
        switch (type) {
            case TYPE_1:
                return type1Points;
            case TYPE_2:
                return type2Points;
            case TYPE_3:
                return type3Points;
            default:
                return 0;
        }
    }
}
