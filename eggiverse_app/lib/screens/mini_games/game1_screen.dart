import 'dart:async';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/game_provider.dart';

class Game1Screen extends StatefulWidget {
  const Game1Screen({super.key});

  @override
  State<Game1Screen> createState() => _Game1ScreenState();
}

class _Game1ScreenState extends State<Game1Screen> {
  int score = 0;
  int timeLeft = 30;
  bool isPlaying = false;
  Timer? gameTimer;
  List<StarPosition> stars = [];
  final Random random = Random();

  @override
  void dispose() {
    gameTimer?.cancel();
    super.dispose();
  }

  void startGame() {
    setState(() {
      score = 0;
      timeLeft = 30;
      isPlaying = true;
      stars = [];
    });

    // 별 생성
    _generateStars();

    // 타이머 시작
    gameTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() {
        timeLeft--;
        if (timeLeft <= 0) {
          _endGame();
        }
      });
    });
  }

  void _generateStars() {
    stars.clear();
    for (int i = 0; i < 5; i++) {
      stars.add(StarPosition(
        left: random.nextDouble() * 300,
        top: random.nextDouble() * 400,
      ));
    }
  }

  void _tapStar(int index) {
    if (!isPlaying) return;

    setState(() {
      score += 10;
      stars[index] = StarPosition(
        left: random.nextDouble() * 300,
        top: random.nextDouble() * 400,
      );
    });
  }

  void _endGame() {
    gameTimer?.cancel();
    setState(() {
      isPlaying = false;
    });

    final game = Provider.of<GameProvider>(context, listen: false);
    game.completeMinigame(score);

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF16213e),
        title: const Text(
          '게임 종료!',
          style: TextStyle(color: Colors.white),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.stars, size: 80, color: Colors.amber),
            const SizedBox(height: 16),
            Text(
              '점수: $score',
              style: const TextStyle(
                color: Colors.white,
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            Text(
              '보상: ${score ~/ 10} 코인',
              style: const TextStyle(color: Colors.amber, fontSize: 18),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              Navigator.pop(context);
            },
            child: const Text('닫기', style: TextStyle(color: Colors.white70)),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              startGame();
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.amber),
            child: const Text('다시하기'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/backgrounds/minigame1_background.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              _buildHeader(),
              _buildGameInfo(),
              Expanded(
                child: isPlaying
                    ? _buildGameArea()
                    : _buildStartScreen(),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            icon: const Icon(Icons.arrow_back, color: Colors.white),
            onPressed: () => Navigator.pop(context),
          ),
          const Text(
            '별 터치 게임',
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

  Widget _buildGameInfo() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildInfoCard('점수', score.toString(), Icons.star, Colors.amber),
          _buildInfoCard('시간', '${timeLeft}s', Icons.timer, Colors.cyan),
        ],
      ),
    );
  }

  Widget _buildInfoCard(String label, String value, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color, width: 2),
      ),
      child: Row(
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(width: 8),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                label,
                style: TextStyle(color: color.withOpacity(0.8), fontSize: 12),
              ),
              Text(
                value,
                style: TextStyle(
                  color: color,
                  fontSize: 20,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildStartScreen() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.stars, size: 100, color: Colors.amber),
          const SizedBox(height: 24),
          const Text(
            '30초 안에 별을 터치하세요!',
            style: TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          const Text(
            '별 하나당 10점!',
            style: TextStyle(color: Colors.amber, fontSize: 16),
          ),
          const SizedBox(height: 40),
          ElevatedButton(
            onPressed: startGame,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.amber,
              padding: const EdgeInsets.symmetric(horizontal: 48, vertical: 16),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(20),
              ),
            ),
            child: const Text(
              '게임 시작',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildGameArea() {
    return Container(
      margin: const EdgeInsets.all(20),
      decoration: BoxDecoration(
        color: const Color(0xFF0a0a15).withOpacity(0.8),
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: Colors.cyan.withOpacity(0.5), width: 2),
      ),
      child: Stack(
        children: stars.asMap().entries.map((entry) {
          int index = entry.key;
          StarPosition star = entry.value;
          return Positioned(
            left: star.left,
            top: star.top,
            child: GestureDetector(
              onTap: () => _tapStar(index),
              child: TweenAnimationBuilder(
                tween: Tween<double>(begin: 0.8, end: 1.2),
                duration: const Duration(milliseconds: 500),
                curve: Curves.easeInOut,
                builder: (context, double scale, child) {
                  return Transform.scale(
                    scale: scale,
                    child: const Icon(
                      Icons.star,
                      size: 50,
                      color: Colors.amber,
                    ),
                  );
                },
                onEnd: () {
                  if (mounted) setState(() {});
                },
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

class StarPosition {
  final double left;
  final double top;

  StarPosition({required this.left, required this.top});
}
