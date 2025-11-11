enum ItemType {
  food,      // 음식
  toy,       // 장난감
  decoration // 장식 (우주선 인테리어)
}

class ShopItem {
  final String id;
  final String name;
  final String description;
  final int price;
  final ItemType type;
  final String? imagePath;
  final int effectValue; // 음식이면 배고픔 회복량, 장난감이면 행복도 증가량
  
  ShopItem({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.type,
    this.imagePath,
    this.effectValue = 10,
  });
  
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'price': price,
      'type': type.toString(),
      'imagePath': imagePath,
      'effectValue': effectValue,
    };
  }
  
  factory ShopItem.fromJson(Map<String, dynamic> json) {
    return ShopItem(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      price: json['price'],
      type: ItemType.values.firstWhere(
        (e) => e.toString() == json['type']
      ),
      imagePath: json['imagePath'],
      effectValue: json['effectValue'] ?? 10,
    );
  }
}

// 기본 상점 아이템 목록
class ShopData {
  static List<ShopItem> getDefaultItems() {
    return [
      // 음식
      ShopItem(
        id: 'food_basic',
        name: '기본 사료',
        description: '알의 배고픔을 채워줍니다',
        price: 10,
        type: ItemType.food,
        effectValue: 20,
      ),
      ShopItem(
        id: 'food_premium',
        name: '프리미엄 사료',
        description: '맛있는 고급 사료!',
        price: 50,
        type: ItemType.food,
        effectValue: 50,
      ),
      
      // 장난감
      ShopItem(
        id: 'toy_ball',
        name: '공',
        description: '알이 좋아하는 공',
        price: 30,
        type: ItemType.toy,
        effectValue: 15,
      ),
      ShopItem(
        id: 'toy_robot',
        name: '로봇 친구',
        description: '같이 놀아줄 로봇',
        price: 100,
        type: ItemType.toy,
        effectValue: 30,
      ),
      
      // 장식
      ShopItem(
        id: 'deco_plant',
        name: '우주 식물',
        description: '우주선을 꾸며줄 식물',
        price: 80,
        type: ItemType.decoration,
        effectValue: 0,
      ),
      ShopItem(
        id: 'deco_poster',
        name: '포스터',
        description: '멋진 우주 포스터',
        price: 40,
        type: ItemType.decoration,
        effectValue: 0,
      ),
    ];
  }
}
