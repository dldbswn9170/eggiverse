import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/game_provider.dart';
import '../models/shop_item.dart';

class MyRoomScreen extends StatelessWidget {
  const MyRoomScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF0f0e17), Color(0xFF1a1a2e), Color(0xFF16213e)],
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              _buildHeader(context),
              Expanded(
                child: _buildSpaceshipInterior(context),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: () => Navigator.pop(context),
          ),
          const Text(
            '마이룸 (우주선)',
            style: TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildSpaceshipInterior(BuildContext context) {
    return Consumer<GameProvider>(
      builder: (context, game, child) {
        final decorations = game.decorations;

        return Stack(
          children: [
            // 우주선 배경
            Center(
              child: Container(
                margin: const EdgeInsets.all(20),
                decoration: BoxDecoration(
                  color: const Color(0xFF2a2a3e).withOpacity(0.8),
                  borderRadius: BorderRadius.circular(30),
                  border: Border.all(
                    color: Colors.cyan.withOpacity(0.5),
                    width: 3,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.cyan.withOpacity(0.3),
                      blurRadius: 20,
                      spreadRadius: 2,
                    ),
                  ],
                ),
                child: Column(
                  children: [
                    // 상단 패널
                    Container(
                      height: 60,
                      decoration: BoxDecoration(
                        color: const Color(0xFF1a1a2e),
                        borderRadius: const BorderRadius.only(
                          topLeft: Radius.circular(27),
                          topRight: Radius.circular(27),
                        ),
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          _buildIndicatorLight(Colors.red),
                          _buildIndicatorLight(Colors.yellow),
                          _buildIndicatorLight(Colors.green),
                        ],
                      ),
                    ),

                    // 창문 (우주 뷰)
                    Expanded(
                      child: Container(
                        margin: const EdgeInsets.all(20),
                        decoration: BoxDecoration(
                          color: const Color(0xFF0a0a15),
                          borderRadius: BorderRadius.circular(20),
                          border: Border.all(
                            color: Colors.grey.shade800,
                            width: 4,
                          ),
                        ),
                        child: Stack(
                          children: [
                            // 별 배경
                            ...List.generate(20, (index) {
                              return Positioned(
                                left: (index * 37) % 300.0,
                                top: (index * 53) % 400.0,
                                child: Icon(
                                  Icons.star,
                                  size: (index % 3 + 1) * 3.0,
                                  color: Colors.white.withOpacity(0.7),
                                ),
                              );
                            }),

                            // 장식품 표시
                            if (decorations.isNotEmpty)
                              Center(
                                child: Wrap(
                                  spacing: 20,
                                  runSpacing: 20,
                                  children: decorations.map((decoId) {
                                    return _buildDecoration(context, decoId, game);
                                  }).toList(),
                                ),
                              ),

                            // 빈 방일 때 메시지
                            if (decorations.isEmpty)
                              Center(
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.center,
                                  children: [
                                    Icon(
                                      Icons.rocket_launch,
                                      size: 80,
                                      color: Colors.white.withOpacity(0.3),
                                    ),
                                    const SizedBox(height: 16),
                                    Text(
                                      '상점에서 장식품을 구매해보세요!',
                                      style: TextStyle(
                                        color: Colors.white.withOpacity(0.5),
                                        fontSize: 16,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                          ],
                        ),
                      ),
                    ),

                    // 하단 패널
                    Container(
                      height: 80,
                      padding: const EdgeInsets.symmetric(horizontal: 20),
                      decoration: BoxDecoration(
                        color: const Color(0xFF1a1a2e),
                        borderRadius: const BorderRadius.only(
                          bottomLeft: Radius.circular(27),
                          bottomRight: Radius.circular(27),
                        ),
                      ),
                      child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                        children: [
                          _buildControlButton(Icons.lightbulb_outline, '조명'),
                          _buildControlButton(Icons.thermostat, '온도'),
                          _buildControlButton(Icons.air, '환기'),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildIndicatorLight(Color color) {
    return Container(
      width: 12,
      height: 12,
      decoration: BoxDecoration(
        color: color,
        shape: BoxShape.circle,
        boxShadow: [
          BoxShadow(
            color: color.withOpacity(0.8),
            blurRadius: 8,
            spreadRadius: 2,
          ),
        ],
      ),
    );
  }

  Widget _buildControlButton(IconData icon, String label) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Icon(icon, color: Colors.cyan, size: 28),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            color: Colors.cyan.withOpacity(0.7),
            fontSize: 12,
          ),
        ),
      ],
    );
  }

  Widget _buildDecoration(BuildContext context, String decoId, GameProvider game) {
    final item = ShopData.getDefaultItems().firstWhere(
      (i) => i.id == decoId,
      orElse: () => ShopData.getDefaultItems().first,
    );

    return GestureDetector(
      onLongPress: () {
        showDialog(
          context: context,
          builder: (context) => AlertDialog(
            backgroundColor: const Color(0xFF16213e),
            title: const Text(
              '장식 제거',
              style: TextStyle(color: Colors.white),
            ),
            content: Text(
              '${item.name}을(를) 제거하시겠습니까?',
              style: const TextStyle(color: Colors.white70),
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('취소', style: TextStyle(color: Colors.white70)),
              ),
              ElevatedButton(
                onPressed: () {
                  game.removeDecoration(decoId);
                  Navigator.pop(context);
                },
                style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
                child: const Text('제거'),
              ),
            ],
          ),
        );
      },
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.purple.withOpacity(0.3),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.purple),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.yard, color: Colors.purple, size: 40),
            const SizedBox(height: 4),
            Text(
              item.name,
              style: const TextStyle(color: Colors.white, fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
}
