package com.eggiverse.app.event;

public class RandomEvent {
    private final String id;
    private final String title;
    private final String description;
    private final EventChoice[] choices;

    public RandomEvent(String id, String title, String description, EventChoice[] choices) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.choices = choices;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventChoice[] getChoices() {
        return choices;
    }

    public static class EventChoice {
        private final String text;
        private final String statType;  // 진화 타입 (예: "adventurer", "scholar")
        private final int statValue;    // 스탯 증가량
        private final String description; // 선택지 설명

        public EventChoice(String text, String statType, int statValue, String description) {
            this.text = text;
            this.statType = statType;
            this.statValue = statValue;
            this.description = description;
        }

        public String getText() {
            return text;
        }

        public String getStatType() {
            return statType;
        }

        public int getStatValue() {
            return statValue;
        }

        public String getDescription() {
            return description;
        }
    }
}
