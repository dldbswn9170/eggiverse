import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/game_provider.dart';
import '../widgets/pixel_egg_widget.dart';
import 'shop_screen.dart';
import 'my_room_screen.dart';
import 'game_select_screen.dart';

class MainScreen extends StatelessWidget {
  const MainScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [Color(0xFF1a1a2e), Color(0xFF16213e), Color(0xFF0f3460)],
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              _buildStatusBar(context),
              Expanded(
                child: Center(
                  child: _buildEggArea(context),
                ),
              ),
              _buildActionButtons(context),
              const SizedBox(height: 20),
              _buildNavigationBar(context),
              const SizedBox(height: 20),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatusBar(BuildContext context) {
    return Consumer<GameProvider>(
      builder: (context, game, child) {
        final egg = game.egg;
        return Container(
          padding: const EdgeInsets.all(16),
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceAround,
                children: [
                  _buildStatCard('레벨', egg.level.toString(), Icons.star),
                  _buildStatCard('코인', game.coins.toString(), Icons.monetization_on),
                  _buildStatCard(
                    '경험치',
                    '${egg.experience}/${egg.getRequiredExp()}',
                    Icons.trending_up,
                  ),
                ],
              ),
              const SizedBox(height: 12),
              _buildProgressBar('배고픔', egg.hunger, Colors.orange),
              const SizedBox(height: 8),
              _buildProgressBar('행복도', egg.happiness, Colors.pink),
            ],
          ),
        );
      },
    );
  }

  Widget _buildStatCard(String label, String value, IconData icon) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.white.withOpacity(0.3)),
      ),
      child: Column(
        children: [
          Icon(icon, color: Colors.white70, size: 20),
          const SizedBox(height: 4),
          Text(
            label,
            style: const TextStyle(color: Colors.white70, fontSize: 12),
          ),
          Text(
            value,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildProgressBar(String label, int value, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              label,
              style: const TextStyle(color: Colors.white70, fontSize: 14),
            ),
            Text(
              '$value%',
              style: const TextStyle(color: Colors.white, fontSize: 14),
            ),
          ],
        ),
        const SizedBox(height: 4),
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: LinearProgressIndicator(
            value: value / 100,
            backgroundColor: Colors.white.withOpacity(0.1),
            valueColor: AlwaysStoppedAnimation<Color>(color),
            minHeight: 10,
          ),
        ),
      ],
    );
  }

  Widget _buildEggArea(BuildContext context) {
    return Consumer<GameProvider>(
      builder: (context, game, child) {
        return PixelEggWidget(
          level: game.egg.level,
          onTap: () {
            game.playWithEgg(5);
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(
                content: Text('알과 놀아줬습니다! 행복도 +5'),
                duration: Duration(seconds: 1),
              ),
            );
          },
        );
      },
    );
  }

  Widget _buildActionButtons(BuildContext context) {
    return Consumer<GameProvider>(
      builder: (context, game, child) {
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              _buildActionButton(
                '먹이주기',
                Icons.restaurant,
                Colors.orange,
                () {
                  game.feedEgg(20);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('먹이를 주었습니다! 배고픔 +20'),
                      duration: Duration(seconds: 1),
                    ),
                  );
                },
              ),
              _buildActionButton(
                '놀아주기',
                Icons.celebration,
                Colors.pink,
                () {
                  game.playWithEgg(20);
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(
                      content: Text('놀아줬습니다! 행복도 +20'),
                      duration: Duration(seconds: 1),
                    ),
                  );
                },
              ),
            ],
          ),
        );
      },
    );
  }

  Widget _buildActionButton(
    String label,
    IconData icon,
    Color color,
    VoidCallback onPressed,
  ) {
    return ElevatedButton(
      onPressed: onPressed,
      style: ElevatedButton.styleFrom(
        backgroundColor: color,
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, color: Colors.white),
          const SizedBox(height: 4),
          Text(
            label,
            style: const TextStyle(color: Colors.white, fontSize: 14),
          ),
        ],
      ),
    );
  }

  Widget _buildNavigationBar(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20),
      padding: const EdgeInsets.symmetric(vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.1),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.white.withOpacity(0.3)),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildNavButton(context, '상점', Icons.store, () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const ShopScreen()),
            );
          }),
          _buildNavButton(context, '마이룸', Icons.home, () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const MyRoomScreen()),
            );
          }),
          _buildNavButton(context, '게임', Icons.games, () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => const GameSelectScreen()),
            );
          }),
        ],
      ),
    );
  }

  Widget _buildNavButton(
    BuildContext context,
    String label,
    IconData icon,
    VoidCallback onPressed,
  ) {
    return InkWell(
      onTap: onPressed,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, color: Colors.white, size: 28),
            const SizedBox(height: 4),
            Text(
              label,
              style: const TextStyle(color: Colors.white, fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
}
