import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/game_provider.dart';
import 'screens/main_screen.dart';

void main() {
  runApp(const EggGame());
}

class EggGame extends StatelessWidget {
  const EggGame({super.key});

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => GameProvider(),
      child: MaterialApp(
        title: '알 키우기',
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
          primarySwatch: Colors.purple,
          brightness: Brightness.dark,
          fontFamily: 'Monospace',
        ),
        home: const MainScreen(),
      ),
    );
  }
}
