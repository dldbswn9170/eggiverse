import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/game_provider.dart';
import 'screens/main_screen.dart';

void main() {
  runApp(
    ChangeNotifierProvider(
      create: (context) => GameProvider(),
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Eggiverse',
      theme: ThemeData(
        primaryColor: const Color(0xFF2D1B4E),
        scaffoldBackgroundColor: const Color(0xFF1A0F2E),
      ),
      home: const MainScreen(),
      debugShowCheckedModeBanner: false,
    );
  }
}
