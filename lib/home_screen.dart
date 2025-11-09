import 'package:flutter/material.dart';
import 'dart:async';
import 'dart:math';

class HomeScreen extends StatefulWidget {
  const HomeScreen({Key? key}) : super(key: key);

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final TextEditingController _textController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  List<Message> messages = [
    Message(
      id: '1',
      type: MessageType.ai,
      text: '안녕! 나는 아직 부화하지 않은 알이야 🥚',
    ),
  ];

  final List<String> dummyResponses = [
    '안녕! 나는 아직 부화하지 않은 알이야 🥚',
    '배가 고파... 뭔가 맛있는 걸 사줄 수 있어?',
    '언제쯤 부화할 수 있을까? 궁금해!',
    '너와 대화하는 게 정말 재미있어!',
    '미니게임 같이 하고 싶어! 재미있을 것 같아!',
    '상점에서 뭔가 특별한 걸 사줄래?',
    '오늘 기분이 어때? 나는 설레!',
    '부화하면 어떤 모습일지 상상해봐!',
  ];

  int daysLeft = 5;
  int level = 1;
  int hunger = 30;
  int money = 1250;
  bool showChatModal = false;

  void _sendMessage() {
    if (_textController.text.trim().isEmpty) return;

    setState(() {
      messages.add(Message(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        type: MessageType.user,
        text: _textController.text,
      ));
    });

    _textController.clear();

    // 스크롤을 맨 아래로
    Future.delayed(const Duration(milliseconds: 100), () {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    });

    // AI 응답
    Timer(const Duration(seconds: 1), () {
      final random = Random();
      final response = dummyResponses[random.nextInt(dummyResponses.length)];

      setState(() {
        messages.add(Message(
          id: (DateTime.now().millisecondsSinceEpoch + 1).toString(),
          type: MessageType.ai,
          text: response,
        ));
      });

      Future.delayed(const Duration(milliseconds: 100), () {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          image: DecorationImage(
            image: AssetImage('assets/images/game-background.png'),
            fit: BoxFit.cover,
          ),
        ),
        child: SafeArea(
          child: Column(
            children: [
              // 상단 스탯
              _buildTopStats(),

              // 알 섹션
              Expanded(
                child: Center(
                  child: _buildEggSection(),
                ),
              ),

              // 배고픔 바
              _buildHungerSection(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTopStats() {
    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Row(
        children: [
          Expanded(child: _buildStatBox('디데이', '$daysLeft일')),
          const SizedBox(width: 8),
          Expanded(child: _buildStatBox('레벨', '$level')),
          const SizedBox(width: 8),
          Expanded(child: _buildStatBox('소지금', '${money.toString().replaceAllMapped(RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'), (Match m) => '${m[1]},')}원')),
        ],
      ),
    );
  }

  Widget _buildStatBox(String label, String value) {
    return Container(
      padding: const EdgeInsets.all(8),
      child: Column(
        children: [
          Text(
            label,
            style: TextStyle(
              fontSize: 11,
              color: AppColors.cyan,
              fontWeight: FontWeight.w600,
              shadows: [
                Shadow(
                  color: Colors.black.withOpacity(0.8),
                  offset: const Offset(1, 1),
                  blurRadius: 2,
                ),
              ],
            ),
          ),
          const SizedBox(height: 4),
          Text(
            value,
            style: TextStyle(
              fontSize: 14,
              color: AppColors.yellow,
              fontWeight: FontWeight.bold,
              shadows: [
                Shadow(
                  color: Colors.black.withOpacity(0.8),
                  offset: const Offset(1, 1),
                  blurRadius: 2,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildEggSection() {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        const Text(
          '🥚',
          style: TextStyle(fontSize: 120),
        ),
        const SizedBox(height: 20),
        GestureDetector(
          onTap: () {
            setState(() {
              showChatModal = true;
            });
            _showChatDialog();
          },
          child: Stack(
            clipBehavior: Clip.none,
            children: [
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  color: AppColors.cyan,
                  borderRadius: BorderRadius.circular(18),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.25),
                      offset: const Offset(0, 2),
                      blurRadius: 3.84,
                    ),
                  ],
                ),
                child: const Text(
                  '안녕! 나와 대화할래?',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ),
              Positioned(
                top: -8,
                left: 0,
                right: 0,
                child: Center(
                  child: CustomPaint(
                    size: const Size(16, 8),
                    painter: BubbleTailPainter(),
                  ),
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildHungerSection() {
    return Padding(
      padding: const EdgeInsets.all(12.0),
      child: Column(
        children: [
          Text(
            '배고픔',
            style: TextStyle(
              fontSize: 12,
              color: AppColors.cyan,
              fontWeight: FontWeight.w600,
              shadows: [
                Shadow(
                  color: Colors.black.withOpacity(0.8),
                  offset: const Offset(1, 1),
                  blurRadius: 2,
                ),
              ],
            ),
          ),
          const SizedBox(height: 8),
          SizedBox(
            width: MediaQuery.of(context).size.width * 0.8,
            child: ClipRRect(
              borderRadius: BorderRadius.circular(4),
              child: LinearProgressIndicator(
                value: hunger / 100,
                minHeight: 8,
                backgroundColor: AppColors.purple.withOpacity(0.5),
                valueColor: AlwaysStoppedAnimation<Color>(AppColors.pink),
              ),
            ),
          ),
          const SizedBox(height: 4),
          Text(
            '$hunger%',
            style: TextStyle(
              fontSize: 12,
              color: AppColors.cyan,
              fontWeight: FontWeight.w600,
              shadows: [
                Shadow(
                  color: Colors.black.withOpacity(0.8),
                  offset: const Offset(1, 1),
                  blurRadius: 2,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  void _showChatDialog() {
    showDialog(
      context: context,
      builder: (context) => Dialog(
        backgroundColor: Colors.transparent,
        insetPadding: const EdgeInsets.all(20),
        child: Container(
          height: MediaQuery.of(context).size.height * 0.8,
          decoration: BoxDecoration(
            color: AppColors.lightPurple,
            borderRadius: BorderRadius.circular(12),
          ),
          child: Column(
            children: [
              // 헤더
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: AppColors.purple,
                  borderRadius: const BorderRadius.only(
                    topLeft: Radius.circular(12),
                    topRight: Radius.circular(12),
                  ),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      '알과 대화하기',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                      ),
                    ),
                    IconButton(
                      icon: const Icon(Icons.close, color: Colors.white),
                      onPressed: () => Navigator.pop(context),
                    ),
                  ],
                ),
              ),

              // 메시지 리스트
              Expanded(
                child: ListView.builder(
                  controller: _scrollController,
                  padding: const EdgeInsets.all(10),
                  itemCount: messages.length,
                  itemBuilder: (context, index) {
                    final message = messages[index];
                    return _buildMessageBubble(message);
                  },
                ),
              ),

              // 입력창
              Container(
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: AppColors.purple,
                ),
                child: Row(
                  children: [
                    Expanded(
                      child: Container(
                        decoration: BoxDecoration(
                          color: AppColors.darkBg,
                          borderRadius: BorderRadius.circular(18),
                        ),
                        child: TextField(
                          controller: _textController,
                          style: const TextStyle(color: Colors.white),
                          maxLines: null,
                          decoration: const InputDecoration(
                            hintText: '메시지를 입력하세요...',
                            hintStyle: TextStyle(color: Color(0xFF8B7B9E)),
                            border: InputBorder.none,
                            contentPadding: EdgeInsets.symmetric(
                              horizontal: 15,
                              vertical: 10,
                            ),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(width: 6),
                    GestureDetector(
                      onTap: _sendMessage,
                      child: Container(
                        width: 40,
                        height: 40,
                        decoration: BoxDecoration(
                          color: AppColors.pink,
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.send,
                          color: Colors.white,
                          size: 20,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildMessageBubble(Message message) {
    final isUser = message.type == MessageType.user;
    return Align(
      alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 6),
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        decoration: BoxDecoration(
          color: isUser ? AppColors.pink : AppColors.cyan,
          borderRadius: BorderRadius.circular(18),
        ),
        child: Text(
          message.text,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 13,
            height: 1.4,
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _textController.dispose();
    _scrollController.dispose();
    super.dispose();
  }
}

// 메시지 모델
enum MessageType { user, ai }

class Message {
  final String id;
  final MessageType type;
  final String text;

  Message({
    required this.id,
    required this.type,
    required this.text,
  });
}

// 색상 정의
class AppColors {
  static const darkBg = Color(0xFF1A0F2E);
  static const purple = Color(0xFF2D1B4E);
  static const lightPurple = Color(0xFF3D2B5E);
  static const pink = Color(0xFFE84B8A);
  static const cyan = Color(0xFF00D9FF);
  static const yellow = Color(0xFFFFD700);
}

// 말풍선 꼬리 그리기
class BubbleTailPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = AppColors.cyan
      ..style = PaintingStyle.fill;

    final path = Path()
      ..moveTo(size.width / 2, 0)
      ..lineTo(size.width / 2 - 8, size.height)
      ..lineTo(size.width / 2 + 8, size.height)
      ..close();

    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => false;
}