
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/game_provider.dart';

class Game3Screen extends StatefulWidget {
  const Game3Screen({super.key});

  @override
  State<Game3Screen> createState() => _Game3ScreenState();
}

class _Game3ScreenState extends State<Game3Screen> {
  List<int> cardValues = [];
  List<bool> cardFlipped = [];
  List<bool> cardMatched = [];
  int? firstCard;
  int? secondCard;
  int moves = 0;
  int matchedPairs = 0;
  bool isPlaying = false;
  bool canFlip = true;

  final List<IconData> icons = [
    Icons.star,
    Icons.favorite,
    Icons.rocket,
    Icons.pets,
    Icons.wb_sunny,
    Icons.local_florist,
  ];

  void _initializeGame() {
    // 6쌍의 카드 생성 (12장)
    cardValues = [...List.generate(6, (i) => i), ...List.generate(6, (i) => i)];
    cardValues.shuffle();
    cardFlipped = List.generate(12, (index) => false);
    cardMatched = List.generate(12, (index) => false);
    firstCard = null;
    secondCard = null;
    moves = 0;
    matchedPairs = 0;
    isPlaying = true;
    canFlip = true;
  }

  void _onCardTap(int index) {
    if (!isPlaying || !canFlip || cardFlipped[index] || cardMatched[index]) {
      return;
    }

    setState(() {
      cardFlipped[index] = true;

      if (firstCard == null) {
        firstCard = index;
      } else if (secondCard == null) {
        secondCard = index;
        canFlip = false;

        // 두 카드 비교
        Timer(const Duration(milliseconds: 1000), () {
          _checkMatch();
        });
      }
    });
  }

  void _checkMatch() {
    if (firstCard == null || secondCard == null) return;

    setState(() {
      if (cardValues[firstCard!] == cardValues[secondCard!]) {
        // 매치 성공
        cardMatched[firstCard!] = true;
        cardMatched[secondCard!] = true;
        matchedPairs++;

        if (matchedPairs == 6) {
          _onGameCompleted();
        }
      } else {
        // 매치 실패
        cardFlipped[firstCard!] = false;
        cardFlipped[secondCard!] = false;
      }

      firstCard = null;
      secondCard = null;
      moves++;
      canFlip = true;
    });
  }

  void _onGameCompleted() {
    setState(() {
      isPlaying = false;
    });

    int score = (500 - moves * 20).clamp(100, 500);
    final game = Provider.of<GameProvider>(context, listen: false);
    game.completeMinigame(score);

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF16213e),
        title: const Text(
          '게임 완료!',
          style: TextStyle(color: Colors.white),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.psychology, size: 80, color: Colors.pink),
            const SizedBox(height: 16),
            Text(
              '시도 횟수: $moves',
              style: const TextStyle(color: Colors.white, fontSize: 20),
            ),
            const SizedBox(height: 8),
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
              setState(() {
                _initializeGame();
              });
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.pink),
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
            image: AssetImage('assets/images/backgrounds/minigame3_background.png'),
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
                    ? _buildCardGrid()
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
            '기억력 게임',
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
          _buildInfoCard('시도', moves.toString(), Icons.touch_app, Colors.pink),
          _buildInfoCard('매칭', '$matchedPairs/6', Icons.check_circle, Colors.green),
        ],
      ),
    );
  }

  Widget _buildInfoCard(String label, String value, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
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
          const Icon(Icons.psychology, size: 100, color: Colors.pink),
          const SizedBox(height: 24),
          const Text(
            '같은 그림을 찾으세요!',
            style: TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          const Text(
            '6쌍을 모두 찾아보세요!',
            style: TextStyle(color: Colors.pink, fontSize: 16),
          ),
          const SizedBox(height: 40),
          ElevatedButton(
            onPressed: () => setState(() => _initializeGame()),
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.pink,
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

  Widget _buildCardGrid() {
    return Center(
      child: Container(
        constraints: const BoxConstraints(maxWidth: 400),
        padding: const EdgeInsets.all(20),
        child: GridView.builder(
          physics: const NeverScrollableScrollPhysics(),
          shrinkWrap: true,
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 4,
            crossAxisSpacing: 10,
            mainAxisSpacing: 10,
          ),
          itemCount: 12,
          itemBuilder: (context, index) {
            return _buildCard(index);
          },
        ),
      ),
    );
  }

  Widget _buildCard(int index) {
    bool isFlipped = cardFlipped[index] || cardMatched[index];

    return GestureDetector(
      onTap: () => _onCardTap(index),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 300),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: isFlipped
                ? [Colors.pink.withOpacity(0.6), Colors.pink.withOpacity(0.8)]
                : [Colors.grey.withOpacity(0.3), Colors.grey.withOpacity(0.5)],
          ),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(
            color: cardMatched[index] ? Colors.green : (isFlipped ? Colors.pink : Colors.grey),
            width: 2,
          ),
          boxShadow: [
            BoxShadow(
              color: (isFlipped ? Colors.pink : Colors.grey).withOpacity(0.3),
              blurRadius: 8,
              spreadRadius: 1,
            ),
          ],
        ),
        child: Center(
          child: isFlipped
              ? Icon(
                  icons[cardValues[index]],
                  size: 40,
                  color: Colors.white,
                )
              : const Icon(
                  Icons.question_mark,
                  size: 40,
                  color: Colors.white54,
                ),
        ),
      ),
    );
  }
}
