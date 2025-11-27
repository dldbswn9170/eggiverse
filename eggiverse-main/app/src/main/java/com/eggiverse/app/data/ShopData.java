package com.eggiverse.app.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ShopData {

    private static final List<ShopItem> DEFAULT_ITEMS;

    static {
        List<ShopItem> items = new ArrayList<>();

        // ===== 펫 먹이 (FOOD) =====
        items.add(new ShopItem("food_star_berry", "별열매", "달콤한 별 모양 열매 (포만감 +15)", 10, ShopItem.ItemType.FOOD, 15));
        items.add(new ShopItem("food_dream_powder", "꿈가루", "반짝이는 신비한 가루 (포만감 +25)", 30, ShopItem.ItemType.FOOD, 25));
        items.add(new ShopItem("food_galaxy_jelly", "은하젤리", "우주의 맛이 나는 젤리 (포만감 +35)", 50, ShopItem.ItemType.FOOD, 35));
        items.add(new ShopItem("food_moon_cake", "달토끼떡", "달토끼가 만든 프리미엄 떡 (포만감 +45)", 70, ShopItem.ItemType.FOOD, 45));

        // ===== 장난감 (TOY) =====
        items.add(new ShopItem("toy_star_rattle", "별 방울", "별 장식이 달린 흔들방울", 20, ShopItem.ItemType.TOY, 10));
        items.add(new ShopItem("toy_soft_star", "말랑별 토이", "별 모양 말랑이 장난감", 35, ShopItem.ItemType.TOY, 15));
        items.add(new ShopItem("toy_glow_ball", "빛나는 공", "어두운 곳에서 빛나는 공", 70, ShopItem.ItemType.TOY, 25));
        items.add(new ShopItem("toy_jumping_jelly", "점핑젤리", "탱탱하게 튀는 젤리 장난감", 100, ShopItem.ItemType.TOY, 30));

        // ===== 마이룸 (DECORATION) =====
        items.add(new ShopItem("deco_cloud_bed", "구름침대", "푹신한 구름 같은 침대", 150, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_soft_bed", "푹신한 침대", "편안한 일반 침대", 100, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_aurora_lamp", "오로라 스탠드조명", "오로라 빛을 내는 조명", 120, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_flower_point", "플라워포인트", "작은 화분들을 모은 장식", 60, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_mini_bookshelf", "미니책장", "작고 귀여운 책장", 80, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_glass_bookshelf", "유리책장", "투명한 유리 재질 책장", 130, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_egg_frame", "에기액자", "알의 사진을 담을 액자", 70, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_mini_table", "미니테이블", "작은 원형 테이블", 90, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_star_rug", "별빛 러그", "반짝이는 별 패턴 러그", 110, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_moon_light", "달 무드등", "은은하게 빛나는 달 조명", 140, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_earth_mobile", "지구 모빌", "천천히 도는 지구 모빌", 160, ShopItem.ItemType.DECORATION, 0));
        items.add(new ShopItem("deco_silk_curtain", "실크 커튼", "부드럽고 우아한 실크 재질 커튼", 95, ShopItem.ItemType.DECORATION, 0));

        DEFAULT_ITEMS = Collections.unmodifiableList(items);
    }

    private ShopData() {
    }

    public static List<ShopItem> getDefaultItems() {
        return DEFAULT_ITEMS;
    }

    public static ShopItem findById(String id) {
        for (ShopItem item : DEFAULT_ITEMS) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}