package com.eggiverse.app.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EventData {
    
    private static final List<RandomEvent> ALL_EVENTS;
    
    static {
        List<RandomEvent> events = new ArrayList<>();
        
        // 1. 신비한 별똥별
        events.add(new RandomEvent(
            "event_shooting_star",
            "✨ 신비한 별똥별이 떨어졌다!",
            "밤하늘에서 반짝이는 별똥별이 알 근처에 떨어졌어요.\n이 별똥별의 빛은 신비한 에너지를 품고 있는 것 같아요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "별똥별을 만져본다",
                    "adventurer",
                    8,
                    "호기심 +5, 용기 +3"
                ),
                new RandomEvent.EventChoice(
                    "멀리서 조용히 관찰한다",
                    "scholar",
                    8,
                    "지혜 +5, 신중함 +3"
                ),
                new RandomEvent.EventChoice(
                    "별똥별을 주워서 보관한다",
                    "collector",
                    8,
                    "수집욕 +5, 애착 +3"
                )
            }
        ));
        
        // 2. 우주 상인의 방문
        events.add(new RandomEvent(
            "event_space_merchant",
            "🛸 수상한 우주 상인이 나타났다!",
            "작은 UFO를 타고 온 우주 상인이 알에게 다가왔어요.\n\"특별한 물건을 팔고 있는데... 관심 있나요?\"",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "신기한 우주 사탕을 산다",
                    "friendly",
                    8,
                    "달콤함 +5, 사교성 +3"
                ),
                new RandomEvent.EventChoice(
                    "수상해서 거절한다",
                    "loner",
                    8,
                    "경계심 +5, 독립심 +3"
                ),
                new RandomEvent.EventChoice(
                    "상인과 흥정을 시도한다",
                    "merchant",
                    8,
                    "상술 +5, 영리함 +3"
                )
            }
        ));
        
        // 3. 우주 폭풍
        events.add(new RandomEvent(
            "event_space_storm",
            "⚡ 갑작스런 우주 폭풍!",
            "예상치 못한 우주 폭풍이 몰려오고 있어요!\n강한 바람과 빛나는 번개가 알을 위협하고 있어요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "단단히 웅크리고 버틴다",
                    "warrior",
                    8,
                    "인내심 +5, 체력 +3"
                ),
                new RandomEvent.EventChoice(
                    "안전한 곳으로 굴러간다",
                    "agile",
                    8,
                    "민첩성 +5, 생존본능 +3"
                ),
                new RandomEvent.EventChoice(
                    "폭풍을 즐긴다!",
                    "free_spirit",
                    8,
                    "대담함 +5, 자유로움 +3"
                )
            }
        ));
        
        // 4. 외로운 우주 고양이
        events.add(new RandomEvent(
            "event_space_cat",
            "😿 외로운 우주 고양이를 발견했다",
            "별 사이를 떠돌던 작은 우주 고양이가\n알 앞에서 슬픈 표정으로 앉아있어요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "다가가서 위로해준다",
                    "angel",
                    8,
                    "친절함 +5, 공감능력 +3"
                ),
                new RandomEvent.EventChoice(
                    "같이 놀아준다",
                    "social",
                    8,
                    "사교성 +5, 활발함 +3"
                ),
                new RandomEvent.EventChoice(
                    "먹이를 나눠준다",
                    "saint",
                    8,
                    "희생정신 +5, 관대함 +3"
                )
            }
        ));
        
        // 5. 신비한 운석
        events.add(new RandomEvent(
            "event_meteor",
            "💎 빛나는 운석 발견!",
            "알 근처에서 이상하게 빛나는 운석을 발견했어요.\n이 운석에서는 신비한 기운이 느껴져요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "운석을 먹어본다",
                    "power",
                    8,
                    "파워 +5, 대담함 +3"
                ),
                new RandomEvent.EventChoice(
                    "운석을 연구한다",
                    "scientist",
                    8,
                    "지식 +5, 탐구심 +3"
                ),
                new RandomEvent.EventChoice(
                    "운석을 침대 옆에 장식한다",
                    "artist",
                    8,
                    "감성 +5, 예술성 +3"
                )
            }
        ));
        
        // 6. 우주 음악 소리
        events.add(new RandomEvent(
            "event_space_music",
            "🎵 우주에서 들려오는 신비한 음악",
            "어디선가 아름다운 음악 소리가 들려와요.\n별들이 만들어내는 하모니 같아요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "음악에 맞춰 춤을 춘다",
                    "dancer",
                    8,
                    "리듬감 +5, 표현력 +3"
                ),
                new RandomEvent.EventChoice(
                    "조용히 음악을 감상한다",
                    "healer",
                    8,
                    "감수성 +5, 평온함 +3"
                ),
                new RandomEvent.EventChoice(
                    "따라 부르며 노래한다",
                    "singer",
                    8,
                    "음악성 +5, 자신감 +3"
                )
            }
        ));
        
        // 7. 타임캡슐 발견
        events.add(new RandomEvent(
            "event_time_capsule",
            "📦 오래된 우주 타임캡슐!",
            "수백년 전 누군가가 묻어둔 타임캡슐을 발견했어요.\n안에는 옛날 우주인들의 메시지가 담겨있어요.",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "메시지를 천천히 읽는다",
                    "sage",
                    8,
                    "역사의식 +5, 지혜 +3"
                ),
                new RandomEvent.EventChoice(
                    "답장을 써서 다시 묻는다",
                    "dreamer",
                    8,
                    "낭만 +5, 상상력 +3"
                ),
                new RandomEvent.EventChoice(
                    "타임캡슐을 박물관에 기증한다",
                    "leader",
                    8,
                    "사회성 +5, 책임감 +3"
                )
            }
        ));
        
        // 8. 우주 먼지 구름
        events.add(new RandomEvent(
            "event_dust_cloud",
            "✨ 반짝이는 우주 먼지 구름",
            "형형색색으로 빛나는 우주 먼지 구름이\n알을 감싸고 있어요. 간지러워요!",
            new RandomEvent.EventChoice[] {
                new RandomEvent.EventChoice(
                    "먼지 구름 속에서 굴러다닌다",
                    "playful",
                    8,
                    "장난기 +5, 활력 +3"
                ),
                new RandomEvent.EventChoice(
                    "먼지를 모아서 그림을 그린다",
                    "painter",
                    8,
                    "창의성 +5, 예술성 +3"
                ),
                new RandomEvent.EventChoice(
                    "먼지를 피해 깨끗한 곳으로 간다",
                    "perfectionist",
                    8,
                    "깔끔함 +5, 완벽주의 +3"
                )
            }
        ));
        
        ALL_EVENTS = Collections.unmodifiableList(events);
    }
    
    private EventData() {}
    
    public static List<RandomEvent> getAllEvents() {
        return ALL_EVENTS;
    }
    
    public static RandomEvent getEventById(String id) {
        for (RandomEvent event : ALL_EVENTS) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }
}
