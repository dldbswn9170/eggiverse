import 'package:flutter/material.dart';

class PixelEggWidget extends StatefulWidget {
  final int level;
  final double size;
  final VoidCallback? onTap;

  const PixelEggWidget({
    Key? key,
    required this.level,
    this.size = 200,
    this.onTap,
  }) : super(key: key);

  @override
  State<PixelEggWidget> createState() => _PixelEggWidgetState();
}

class _PixelEggWidgetState extends State<PixelEggWidget>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _bounceAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 2000),
      vsync: this,
    )..repeat(reverse: true);

    _bounceAnimation = Tween<double>(begin: 0, end: 10).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  String _getCharacterImage() {
    // 레벨에 따라 캐릭터 이미지 변경
    if (widget.level < 10) {
      return 'assets/images/characters/character1.jpg';
    } else {
      return 'assets/images/characters/character2.jpg';
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.onTap,
      child: AnimatedBuilder(
        animation: _bounceAnimation,
        builder: (context, child) {
          return Transform.translate(
            offset: Offset(0, -_bounceAnimation.value),
            child: Container(
              width: widget.size,
              height: widget.size,
              decoration: BoxDecoration(
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.3),
                    blurRadius: 20,
                    spreadRadius: 5,
                    offset: const Offset(0, 10),
                  ),
                ],
              ),
              child: Image.asset(
                _getCharacterImage(),
                fit: BoxFit.contain,
                filterQuality: FilterQuality.none, // 픽셀 아트를 선명하게
              ),
            ),
          );
        },
      ),
    );
  }
}
