import 'package:flutter/material.dart';
import 'home_screen.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: '알 키우기',
      theme: ThemeData(
        primaryColor: const Color(0xFF2D1B4E),
        scaffoldBackgroundColor: const Color(0xFF1A0F2E),
        fontFamily: 'Pretendard', // 한글 폰트 설정
      ),
      home: const MainScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}

class MainScreen extends StatefulWidget {
  const MainScreen({Key? key}) : super(key: key);

  @override
  State<MainScreen> createState() => _MainScreenState();
}

class _MainScreenState extends State<MainScreen> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const HomeScreen(),
    const MinigameScreen(),
    const ShopScreen(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: Container(
        decoration: const BoxDecoration(
          border: Border(
            top: BorderSide(
              color: Color(0xFFE84B8A),
              width: 2,
            ),
          ),
        ),
        child: BottomNavigationBar(
          currentIndex: _currentIndex,
          onTap: (index) {
            setState(() {
              _currentIndex = index;
            });
          },
          backgroundColor: const Color(0xFF2D1B4E),
          selectedItemColor: const Color(0xFFE84B8A),
          unselectedItemColor: const Color(0xFF8B7B9E),
          selectedFontSize: 12,
          unselectedFontSize: 12,
          type: BottomNavigationBarType.fixed,
          elevation: 0,
          items: const [
            BottomNavigationBarItem(
              icon: Icon(Icons.home),
              label: '홈',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.games),
              label: 'MiniGame',
            ),
            BottomNavigationBarItem(
              icon: Icon(Icons.shopping_bag),
              label: '상점',
            ),
          ],
        ),
      ),
    );
  }
}

// 미니게임 화면 (플레이스홀더)
class MinigameScreen extends StatelessWidget {
  const MinigameScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('미니게임'),
        backgroundColor: const Color(0xFF2D1B4E),
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/game-background.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: const Center(
          child: Text(
            '미니게임 화면',
            style: TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}

// 상점 화면 (플레이스홀더)
class ShopScreen extends StatelessWidget {
  const ShopScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('상점'),
        backgroundColor: const Color(0xFF2D1B4E),
      ),
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/game-background.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: const Center(
          child: Text(
            '상점 화면',
            style: TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
        ),
      ),
    );
  }
}