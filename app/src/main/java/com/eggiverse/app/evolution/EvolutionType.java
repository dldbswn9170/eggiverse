package com.eggiverse.app.evolution;

/**
 * 진화 타입 정의
 * 3가지 진화 경로를 제공합니다: 모험가, 학자, 수집가 등
 */
public enum EvolutionType {
    TYPE_1("type_1", "타입 1"),      // 예: 모험가 경로
    TYPE_2("type_2", "타입 2"),      // 예: 학자 경로
    TYPE_3("type_3", "타입 3");      // 예: 수집가 경로

    private final String id;
    private final String displayName;

    EvolutionType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EvolutionType fromId(String id) {
        for (EvolutionType type : EvolutionType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return TYPE_1; // 기본값
    }
}
