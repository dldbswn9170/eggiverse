package com.eggiverse.app.event;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomEventManager {

    private static RandomEventManager instance;
    private final SharedPreferences prefs;
    private final Random random;

    private static final String PREFS_NAME = "event_prefs";
    private static final String KEY_LAST_EVENT_TIME = "last_event_time";
    private static final String KEY_EVENT_COUNT_TODAY = "event_count_today";
    private static final String KEY_LAST_DATE = "last_date";
    private static final String KEY_SHOWN_EVENTS_TODAY = "shown_events_today";

    // 테스트용 카운터
    private static final String KEY_FEED_COUNT = "feed_count";
    private static final String KEY_SHOP_BUY_COUNT = "shop_buy_count";

    // 설정값
    private static final int EVENT_PROBABILITY = 25; // 25% 확률
    private static final int MAX_EVENTS_PER_DAY = 3; // 하루 최대 3회
    private static final long MIN_EVENT_INTERVAL = 10 * 60 * 1000; // 10분 (밀리초)

    private RandomEventManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        random = new Random();
        checkAndResetDailyCount();
    }

    public static synchronized RandomEventManager getInstance(Context context) {
        if (instance == null) {
            instance = new RandomEventManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 이벤트 발생 여부 체크
     * @return 이벤트를 발생시켜야 하면 true
     */
    public boolean shouldTriggerEvent() {
        checkAndResetDailyCount();

        // 1. 오늘 이미 최대 횟수 달성?
        int todayCount = prefs.getInt(KEY_EVENT_COUNT_TODAY, 0);
        if (todayCount >= MAX_EVENTS_PER_DAY) {
            return false;
        }

        // 2. 마지막 이벤트로부터 충분한 시간이 지났는가?
        long lastEventTime = prefs.getLong(KEY_LAST_EVENT_TIME, 0);
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastEventTime < MIN_EVENT_INTERVAL) {
            return false;
        }

        // 3. 확률 체크 (25%)
        int randomValue = random.nextInt(100);
        return randomValue < EVENT_PROBABILITY;
    }

    /**
     * 테스트용: 먹이 주기 이벤트 체크 (3번째에 확정 발동)
     */
    public boolean shouldTriggerEventOnFeed() {
        int feedCount = prefs.getInt(KEY_FEED_COUNT, 0);
        feedCount++;
        prefs.edit().putInt(KEY_FEED_COUNT, feedCount).apply();

        // 3번째 먹이에 확정 발동
        if (feedCount == 3) {
            prefs.edit().putInt(KEY_FEED_COUNT, 0).apply(); // 카운터 리셋
            return true;
        }

        return shouldTriggerEvent(); // 기본 확률 체크
    }

    /**
     * 테스트용: 상점 구매 이벤트 체크 (2번째에 확정 발동)
     */
    public boolean shouldTriggerEventOnShopBuy() {
        int buyCount = prefs.getInt(KEY_SHOP_BUY_COUNT, 0);
        buyCount++;
        prefs.edit().putInt(KEY_SHOP_BUY_COUNT, buyCount).apply();

        // 2번째 구매에 확정 발동
        if (buyCount == 2) {
            prefs.edit().putInt(KEY_SHOP_BUY_COUNT, 0).apply(); // 카운터 리셋
            return true;
        }

        return shouldTriggerEvent(); // 기본 확률 체크
    }

    /**
     * 랜덤 이벤트 가져오기 (오늘 아직 안 본 이벤트 중에서)
     */
    public RandomEvent getRandomEvent() {
        List<RandomEvent> allEvents = EventData.getAllEvents();
        Set<String> shownToday = getShownEventsToday();

        // 아직 안 본 이벤트만 필터링
        List<RandomEvent> availableEvents = new java.util.ArrayList<>();
        for (RandomEvent event : allEvents) {
            if (!shownToday.contains(event.getId())) {
                availableEvents.add(event);
            }
        }

        // 모든 이벤트를 다 봤으면 리셋
        if (availableEvents.isEmpty()) {
            shownToday.clear();
            saveShownEventsToday(shownToday);
            availableEvents.addAll(allEvents);
        }

        // 랜덤하게 하나 선택
        if (availableEvents.isEmpty()) {
            return null;
        }

        int randomIndex = random.nextInt(availableEvents.size());
        return availableEvents.get(randomIndex);
    }

    /**
     * 이벤트 발생 기록
     */
    public void recordEventShown(String eventId) {
        // 마지막 이벤트 시간 업데이트
        prefs.edit()
                .putLong(KEY_LAST_EVENT_TIME, System.currentTimeMillis())
                .apply();

        // 오늘 카운트 증가
        int todayCount = prefs.getInt(KEY_EVENT_COUNT_TODAY, 0);
        prefs.edit()
                .putInt(KEY_EVENT_COUNT_TODAY, todayCount + 1)
                .apply();

        // 오늘 본 이벤트 목록에 추가
        Set<String> shownToday = getShownEventsToday();
        shownToday.add(eventId);
        saveShownEventsToday(shownToday);
    }

    /**
     * 날짜가 바뀌었는지 체크하고 일일 카운트 리셋
     */
    private void checkAndResetDailyCount() {
        String today = getCurrentDate();
        String lastDate = prefs.getString(KEY_LAST_DATE, "");

        if (!today.equals(lastDate)) {
            // 날짜가 바뀜 - 카운트 리셋
            prefs.edit()
                    .putInt(KEY_EVENT_COUNT_TODAY, 0)
                    .putString(KEY_LAST_DATE, today)
                    .apply();

            // 오늘 본 이벤트 목록도 리셋
            Set<String> empty = new HashSet<>();
            saveShownEventsToday(empty);
        }
    }

    /**
     * 현재 날짜를 문자열로 반환 (YYYY-MM-DD)
     */
    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }

    /**
     * 오늘 본 이벤트 ID 목록 가져오기
     */
    private Set<String> getShownEventsToday() {
        String shown = prefs.getString(KEY_SHOWN_EVENTS_TODAY, "");
        Set<String> result = new HashSet<>();
        if (!shown.isEmpty()) {
            String[] ids = shown.split(",");
            for (String id : ids) {
                if (!id.trim().isEmpty()) {
                    result.add(id.trim());
                }
            }
        }
        return result;
    }

    /**
     * 오늘 본 이벤트 ID 목록 저장
     */
    private void saveShownEventsToday(Set<String> events) {
        StringBuilder sb = new StringBuilder();
        for (String id : events) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(id);
        }
        prefs.edit()
                .putString(KEY_SHOWN_EVENTS_TODAY, sb.toString())
                .apply();
    }

    /**
     * 테스트용 - 강제로 이벤트 발생 (디버깅용)
     */
    public void forceResetForTesting() {
        prefs.edit()
                .putInt(KEY_EVENT_COUNT_TODAY, 0)
                .putLong(KEY_LAST_EVENT_TIME, 0)
                .apply();
    }
}