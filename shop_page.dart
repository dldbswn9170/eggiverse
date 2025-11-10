import 'package:flutter/material.dart';

// 아이템 데이터 모델
class ShopItem {
  final String id;
  final String name;
  final String description;
  final int price;
  final String iconPath; // 아이콘 이미지 경로
  final String category; // 'pet', 'room', 'plus'

  ShopItem({
    required this.id,
    required this.name,
    required this.description,
    required this.price,
    required this.iconPath,
    required this.category,
  });
}

class ShopPage extends StatefulWidget {
  const ShopPage({Key? key}) : super(key: key);

  @override
  State<ShopPage> createState() => _ShopPageState();
}

class _ShopPageState extends State<ShopPage> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  int _currentMoney = 10000; // 현재 보유 금액 (예시)

  // 샘플 아이템 데이터
  final List<ShopItem> _allItems = [
    // 펫 아이템
    ShopItem(
      id: 'pet_1',
      name: '별가루 사료',
      description: '알의 에너지를 회복시켜줍니다',
      price: 100,
      iconPath: 'assets/items/star_dust.png',
      category: 'pet',
    ),
    ShopItem(
      id: 'pet_2',
      name: '행성 과일',
      description: '알의 성장을 촉진합니다',
      price: 200,
      iconPath: 'assets/items/planet_fruit.png',
      category: 'pet',
    ),
    ShopItem(
      id: 'pet_3',
      name: '암석 에너지',
      description: '희귀 진화 확률 증가',
      price: 500,
      iconPath: 'assets/items/rock_energy.png',
      category: 'pet',
    ),
    // 마이룸 아이템
    ShopItem(
      id: 'room_1',
      name: '별자리 포스터',
      description: '우주선을 꾸며줍니다',
      price: 300,
      iconPath: 'assets/items/constellation_poster.png',
      category: 'room',
    ),
    ShopItem(
      id: 'room_2',
      name: '유성우 램프',
      description: '분위기 있는 조명',
      price: 400,
      iconPath: 'assets/items/meteor_lamp.png',
      category: 'room',
    ),
    ShopItem(
      id: 'room_3',
      name: '외계 식물',
      description: '신비로운 장식물',
      price: 250,
      iconPath: 'assets/items/alien_plant.png',
      category: 'room',
    ),
    // 아이템+
    ShopItem(
      id: 'plus_1',
      name: '시간 가속기',
      description: '알의 성장 속도 2배 (1시간)',
      price: 1000,
      iconPath: 'assets/items/time_booster.png',
      category: 'plus',
    ),
    ShopItem(
      id: 'plus_2',
      name: '럭키 부스터',
      description: '희귀 이벤트 발생률 증가',
      price: 800,
      iconPath: 'assets/items/lucky_booster.png',
      category: 'plus',
    ),
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  // 카테고리별 아이템 필터링
  List<ShopItem> _getItemsByCategory(String category) {
    return _allItems.where((item) => item.category == category).toList();
  }

  // 구매 확인 다이얼로그 (픽셀아트 스타일)
  void _showPurchaseDialog(ShopItem item) {
    showDialog(
      context: context,
      barrierColor: Colors.black.withOpacity(0.8),
      builder: (BuildContext context) {
        return AlertDialog(
          backgroundColor: Colors.black.withOpacity(0.9),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8),
            side: const BorderSide(color: Color(0xFF6C63FF), width: 3),
          ),
          title: Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: const Color(0xFF6C63FF).withOpacity(0.3),
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: const Color(0xFF6C63FF), width: 2),
            ),
            child: const Text(
              '⚠️  구매 확인  ⚠️',
              style: TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.bold,
                letterSpacing: 2,
              ),
              textAlign: TextAlign.center,
            ),
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // 아이템 정보 박스
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.black.withOpacity(0.5),
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    color: const Color(0xFF6C63FF).withOpacity(0.5),
                    width: 2,
                  ),
                ),
                child: Column(
                  children: [
                    // 아이콘
                    Container(
                      width: 80,
                      height: 80,
                      decoration: BoxDecoration(
                        color: const Color(0xFF6C63FF).withOpacity(0.3),
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(
                          color: const Color(0xFF6C63FF),
                          width: 2,
                        ),
                      ),
                      child: const Icon(
                        Icons.stars,
                        size: 50,
                        color: Color(0xFF6C63FF),
                      ),
                    ),
                    const SizedBox(height: 12),
                    // 아이템 이름
                    Text(
                      item.name,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        shadows: [
                          Shadow(
                            color: Color(0xFF6C63FF),
                            offset: Offset(2, 2),
                            blurRadius: 0,
                          ),
                        ],
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 8),
                    // 가격
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.black.withOpacity(0.7),
                        borderRadius: BorderRadius.circular(6),
                        border: Border.all(
                          color: const Color(0xFFFFD700),
                          width: 2,
                        ),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(
                            Icons.monetization_on,
                            color: Color(0xFFFFD700),
                            size: 20,
                          ),
                          const SizedBox(width: 6),
                          Text(
                            '${item.price}',
                            style: const TextStyle(
                              color: Color(0xFFFFD700),
                              fontSize: 18,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              // 잔액 부족 경고
              if (_currentMoney < item.price)
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.red.withOpacity(0.2),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: Colors.red, width: 2),
                  ),
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: const [
                      Icon(Icons.warning, color: Colors.red, size: 20),
                      SizedBox(width: 8),
                      Text(
                        '코인이 부족합니다!',
                        style: TextStyle(
                          color: Colors.red,
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ],
                  ),
                ),
            ],
          ),
          actions: [
            Row(
              children: [
                // 아니요 버튼
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: Colors.white54, width: 2),
                    ),
                    child: TextButton(
                      onPressed: () {
                        Navigator.of(context).pop();
                      },
                      style: TextButton.styleFrom(
                        backgroundColor: Colors.black.withOpacity(0.5),
                        padding: const EdgeInsets.symmetric(vertical: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                      ),
                      child: const Text(
                        '❌ 아니요',
                        style: TextStyle(
                          color: Colors.white70,
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                // 예 버튼
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: _currentMoney >= item.price
                            ? const Color(0xFF6C63FF)
                            : Colors.grey,
                        width: 2,
                      ),
                      boxShadow: _currentMoney >= item.price
                          ? [
                              BoxShadow(
                                color: const Color(0xFF6C63FF).withOpacity(0.5),
                                blurRadius: 8,
                                spreadRadius: 1,
                              ),
                            ]
                          : null,
                    ),
                    child: ElevatedButton(
                      onPressed: _currentMoney >= item.price
                          ? () {
                              _purchaseItem(item);
                              Navigator.of(context).pop();
                            }
                          : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _currentMoney >= item.price
                            ? const Color(0xFF6C63FF)
                            : Colors.grey.withOpacity(0.5),
                        disabledBackgroundColor: Colors.grey.withOpacity(0.5),
                        padding: const EdgeInsets.symmetric(vertical: 12),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(8),
                        ),
                        elevation: 0,
                      ),
                      child: const Text(
                        '✅ 예',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ],
        );
      },
    );
  }

  // 아이템 구매 처리
  void _purchaseItem(ShopItem item) {
    setState(() {
      _currentMoney -= item.price;
    });

    // 구매 성공 스낵바 (픽셀아트 스타일)
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Container(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Row(
            children: [
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.green,
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.white, width: 2),
                ),
                child: const Icon(
                  Icons.check_circle,
                  color: Colors.white,
                  size: 24,
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text(
                      '✨ 구매 완료!',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    Text(
                      '${item.name}을(를) 획득했습니다',
                      style: const TextStyle(
                        color: Colors.white70,
                        fontSize: 14,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        backgroundColor: Colors.black.withOpacity(0.9),
        behavior: SnackBarBehavior.floating,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
          side: const BorderSide(color: Colors.green, width: 2),
        ),
        duration: const Duration(seconds: 3),
        margin: const EdgeInsets.all(16),
      ),
    );

    // TODO: 실제로는 여기서 구매 데이터를 저장해야 함
    // 예: SharedPreferences, SQLite 등에 저장
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        // 우주 배경 이미지 적용
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/backgrounds/space.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: Column(
          children: [
            // 커스텀 앱바
            _buildCustomAppBar(),
            // 탭 메뉴
            _buildTabMenu(),
            // 아이템 리스트
            Expanded(
              child: TabBarView(
                controller: _tabController,
                children: [
                  _buildItemList('pet'),
                  _buildItemList('room'),
                  _buildItemList('plus'),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 커스텀 앱바 위젯
  Widget _buildCustomAppBar() {
    return SafeArea(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 16),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.6),
          border: Border(
            bottom: BorderSide(
              color: const Color(0xFF6C63FF).withOpacity(0.5),
              width: 2,
            ),
          ),
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            // 뒤로가기 버튼
            Container(
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.5),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFF6C63FF), width: 2),
              ),
              child: IconButton(
                icon: const Icon(Icons.arrow_back, color: Colors.white, size: 24),
                onPressed: () {
                  Navigator.of(context).pop();
                },
                padding: const EdgeInsets.all(8),
                constraints: const BoxConstraints(),
              ),
            ),
            // 상점 타이틀 (픽셀 폰트 스타일)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 8),
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.7),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFF6C63FF), width: 2),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF6C63FF).withOpacity(0.5),
                    blurRadius: 8,
                    spreadRadius: 1,
                  ),
                ],
              ),
              child: const Text(
                '🛒 상  점',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 4,
                  shadows: [
                    Shadow(
                      color: Color(0xFF6C63FF),
                      offset: Offset(2, 2),
                      blurRadius: 0,
                    ),
                  ],
                ),
              ),
            ),
            // 보유 금액
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              decoration: BoxDecoration(
                color: Colors.black.withOpacity(0.7),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFFFD700), width: 2),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFFFFD700).withOpacity(0.5),
                    blurRadius: 8,
                    spreadRadius: 1,
                  ),
                ],
              ),
              child: Row(
                children: [
                  const Icon(Icons.monetization_on, color: Color(0xFFFFD700), size: 20),
                  const SizedBox(width: 4),
                  Text(
                    '$_currentMoney',
                    style: const TextStyle(
                      color: Color(0xFFFFD700),
                      fontSize: 16,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  // 탭 메뉴 위젯
  Widget _buildTabMenu() {
    return Container(
      color: Colors.black.withOpacity(0.5),
      padding: const EdgeInsets.symmetric(vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildTabButton('pet', '🐾 펫 아이템', 0),
          _buildTabButton('room', '🏠 마이룸', 1),
          _buildTabButton('plus', '✨ 아이템+', 2),
        ],
      ),
    );
  }

  // 탭 버튼 위젯
  Widget _buildTabButton(String category, String label, int index) {
    final isSelected = _tabController.index == index;
    
    return GestureDetector(
      onTap: () {
        setState(() {
          _tabController.animateTo(index);
        });
      },
      child: AnimatedBuilder(
        animation: _tabController,
        builder: (context, child) {
          final isActive = _tabController.index == index;
          return Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
            decoration: BoxDecoration(
              color: isActive 
                  ? const Color(0xFF6C63FF).withOpacity(0.8)
                  : Colors.black.withOpacity(0.5),
              borderRadius: BorderRadius.circular(8),
              border: Border.all(
                color: isActive ? const Color(0xFF6C63FF) : Colors.white38,
                width: 2,
              ),
              boxShadow: isActive
                  ? [
                      BoxShadow(
                        color: const Color(0xFF6C63FF).withOpacity(0.6),
                        blurRadius: 12,
                        spreadRadius: 2,
                      ),
                    ]
                  : null,
            ),
            child: Text(
              label,
              style: TextStyle(
                color: isActive ? Colors.white : Colors.white70,
                fontSize: 14,
                fontWeight: isActive ? FontWeight.bold : FontWeight.normal,
              ),
            ),
          );
        },
      ),
    );
  }



  // 아이템 리스트 위젯
  Widget _buildItemList(String category) {
    final items = _getItemsByCategory(category);

    if (items.isEmpty) {
      return Center(
        child: Container(
          padding: const EdgeInsets.all(24),
          decoration: BoxDecoration(
            color: Colors.black.withOpacity(0.6),
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: Colors.white38, width: 2),
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(
                Icons.inventory_2_outlined,
                size: 64,
                color: Colors.white38,
              ),
              const SizedBox(height: 16),
              Text(
                '아이템이 없습니다',
                style: TextStyle(
                  color: Colors.white70,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      );
    }

    return ListView.builder(
      padding: const EdgeInsets.all(12),
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        return _buildItemCard(item);
      },
    );
  }

  // 아이템 카드 위젯 (픽셀아트 스타일)
  Widget _buildItemCard(ShopItem item) {
    return GestureDetector(
      onTap: () => _showPurchaseDialog(item),
      child: Container(
        margin: const EdgeInsets.only(bottom: 12),
        decoration: BoxDecoration(
          color: Colors.black.withOpacity(0.7),
          borderRadius: BorderRadius.circular(8),
          border: Border.all(
            color: const Color(0xFF6C63FF).withOpacity(0.6),
            width: 3,
          ),
          boxShadow: [
            BoxShadow(
              color: const Color(0xFF6C63FF).withOpacity(0.3),
              blurRadius: 8,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: Material(
          color: Colors.transparent,
          child: InkWell(
            onTap: () => _showPurchaseDialog(item),
            borderRadius: BorderRadius.circular(8),
            splashColor: const Color(0xFF6C63FF).withOpacity(0.3),
            child: Padding(
              padding: const EdgeInsets.all(12),
              child: Row(
                children: [
                  // 아이템 아이콘 (픽셀아트 느낌)
                  Container(
                    width: 64,
                    height: 64,
                    decoration: BoxDecoration(
                      color: const Color(0xFF6C63FF).withOpacity(0.2),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: const Color(0xFF6C63FF),
                        width: 2,
                      ),
                    ),
                    child: const Icon(
                      Icons.stars,
                      size: 36,
                      color: Color(0xFF6C63FF),
                    ),
                    // 실제 이미지 사용 시:
                    // child: Image.asset(
                    //   item.iconPath,
                    //   fit: BoxFit.contain,
                    // ),
                  ),
                  const SizedBox(width: 12),
                  // 아이템 정보
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // 아이템 이름
                        Text(
                          item.name,
                          style: const TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            shadows: [
                              Shadow(
                                color: Color(0xFF6C63FF),
                                offset: Offset(1, 1),
                                blurRadius: 0,
                              ),
                            ],
                          ),
                        ),
                        const SizedBox(height: 4),
                        // 아이템 설명
                        Text(
                          item.description,
                          style: const TextStyle(
                            color: Colors.white70,
                            fontSize: 12,
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                        const SizedBox(height: 8),
                        // 가격
                        Container(
                          padding: const EdgeInsets.symmetric(
                            horizontal: 8,
                            vertical: 4,
                          ),
                          decoration: BoxDecoration(
                            color: Colors.black.withOpacity(0.5),
                            borderRadius: BorderRadius.circular(6),
                            border: Border.all(
                              color: const Color(0xFFFFD700),
                              width: 2,
                            ),
                          ),
                          child: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              const Icon(
                                Icons.monetization_on,
                                color: Color(0xFFFFD700),
                                size: 16,
                              ),
                              const SizedBox(width: 4),
                              Text(
                                '${item.price}',
                                style: const TextStyle(
                                  color: Color(0xFFFFD700),
                                  fontSize: 14,
                                  fontWeight: FontWeight.bold,
                                ),
                              ),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  // 구매 화살표
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      color: const Color(0xFF6C63FF).withOpacity(0.3),
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                        color: const Color(0xFF6C63FF),
                        width: 2,
                      ),
                    ),
                    child: const Icon(
                      Icons.arrow_forward_ios,
                      color: Colors.white,
                      size: 20,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}