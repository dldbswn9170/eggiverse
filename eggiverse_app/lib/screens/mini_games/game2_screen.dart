import 'dart:math';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/game_provider.dart';

class Game2Screen extends StatefulWidget {
  const Game2Screen({super.key});

  @override
  State<Game2Screen> createState() => _Game2ScreenState();
}

class _Game2ScreenState extends State<Game2Screen> {
  List<int> tiles = [];
  int moves = 0;
  bool isPlaying = false;
  final Random random = Random();

  @override
  void initState() {
    super.initState();
    _initializePuzzle();
  }

  void _initializePuzzle() {
    tiles = List.generate(9, (index) => index);
  }

  void _shufflePuzzle() {
    setState(() {
      moves = 0;
      isPlaying = true;

      // 퍼즐 섞기
      for (int i = 0; i < 100; i++) {
        int emptyIndex = tiles.indexOf(0);
        List<int> validMoves = _getValidMoves(emptyIndex);
        if (validMoves.isNotEmpty) {
          int randomMove = validMoves[random.nextInt(validMoves.length)];
          _swapTiles(emptyIndex, randomMove);
        }
      }
    });
  }

  List<int> _getValidMoves(int emptyIndex) {
    List<int> validMoves = [];
    int row = emptyIndex ~/ 3;
    int col = emptyIndex % 3;

    // 위
    if (row > 0) validMoves.add(emptyIndex - 3);
    // 아래
    if (row < 2) validMoves.add(emptyIndex + 3);
    // 왼쪽
    if (col > 0) validMoves.add(emptyIndex - 1);
    // 오른쪽
    if (col < 2) validMoves.add(emptyIndex + 1);

    return validMoves;
  }

  void _swapTiles(int index1, int index2) {
    int temp = tiles[index1];
    tiles[index1] = tiles[index2];
    tiles[index2] = temp;
  }

  void _onTileTap(int index) {
    if (!isPlaying) return;

    int emptyIndex = tiles.indexOf(0);
    List<int> validMoves = _getValidMoves(emptyIndex);

    if (validMoves.contains(index)) {
      setState(() {
        _swapTiles(emptyIndex, index);
        moves++;
      });

      if (_isPuzzleSolved()) {
        _onPuzzleCompleted();
      }
    }
  }

  bool _isPuzzleSolved() {
    for (int i = 0; i < tiles.length; i++) {
      if (tiles[i] != i) return false;
    }
    return true;
  }

  void _onPuzzleCompleted() {
    setState(() {
      isPlaying = false;
    });

    int score = max(0, 500 - moves * 10);
    final game = Provider.of<GameProvider>(context, listen: false);
    game.completeMinigame(score);

    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF16213e),
        title: const Text(
          '퍼즐 완성!',
          style: TextStyle(color: Colors.white),
        ),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            const Icon(Icons.celebration, size: 80, color: Colors.blue),
            const SizedBox(height: 16),
            Text(
              '이동 횟수: $moves',
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
              _shufflePuzzle();
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
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
            image: AssetImage('assets/images/backgrounds/minigame2_background.png'),
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
                    ? _buildPuzzleGrid()
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
            '슬라이딩 퍼즐',
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
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: Colors.blue.withOpacity(0.2),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: Colors.blue, width: 2),
            ),
            child: Row(
              children: [
                const Icon(Icons.touch_app, color: Colors.blue, size: 24),
                const SizedBox(width: 8),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '이동',
                      style: TextStyle(
                        color: Colors.blue.withOpacity(0.8),
                        fontSize: 12,
                      ),
                    ),
                    Text(
                      '$moves',
                      style: const TextStyle(
                        color: Colors.blue,
                        fontSize: 20,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ],
            ),
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
          const Icon(Icons.grid_3x3, size: 100, color: Colors.blue),
          const SizedBox(height: 24),
          const Text(
            '숫자를 순서대로 맞춰보세요!',
            style: TextStyle(
              color: Colors.white,
              fontSize: 20,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 16),
          const Text(
            '적은 이동으로 완성할수록 높은 점수!',
            style: TextStyle(color: Colors.blue, fontSize: 16),
          ),
          const SizedBox(height: 40),
          ElevatedButton(
            onPressed: _shufflePuzzle,
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.blue,
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

  Widget _buildPuzzleGrid() {
    return Center(
      child: Container(
        width: 320,
        height: 320,
        margin: const EdgeInsets.all(20),
        child: GridView.builder(
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 3,
            crossAxisSpacing: 8,
            mainAxisSpacing: 8,
          ),
          itemCount: 9,
          itemBuilder: (context, index) {
            return _buildTile(index);
          },
        ),
      ),
    );
  }

  Widget _buildTile(int index) {
    int value = tiles[index];

    if (value == 0) {
      return Container(
        decoration: BoxDecoration(
          color: Colors.white.withOpacity(0.05),
          borderRadius: BorderRadius.circular(12),
        ),
      );
    }

    return GestureDetector(
      onTap: () => _onTileTap(index),
      child: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
            colors: [
              Colors.blue.withOpacity(0.6),
              Colors.blue.withOpacity(0.8),
            ],
          ),
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.blue, width: 2),
          boxShadow: [
            BoxShadow(
              color: Colors.blue.withOpacity(0.3),
              blurRadius: 8,
              spreadRadius: 2,
            ),
          ],
        ),
        child: Center(
          child: Text(
            '$value',
            style: const TextStyle(
              color: Colors.white,
              fontSize: 40,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}
