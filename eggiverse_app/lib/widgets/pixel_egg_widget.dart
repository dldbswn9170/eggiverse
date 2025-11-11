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
  String _message = '안녕! 만나서 반가워!'; // 초기 더미 데이터

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

  void _updateMessage(String newMessage) {
    setState(() {
      _message = newMessage;
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  String _getCharacterImage() {
    // For now, always return the pixel egg image.
    return 'assets/images/characters/pixel_egg.png';
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.onTap,
      child: Stack(
        clipBehavior: Clip.none,
        alignment: Alignment.center,
        children: [
          AnimatedBuilder(
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
                    filterQuality: FilterQuality.none,
                  ),
                ),
              );
            },
          ),
          Positioned(
            bottom: -40,
            child: _buildSpeechBubble(),
          ),
        ],
      ),
    );
  }

  Widget _buildSpeechBubble() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.2),
            blurRadius: 5,
            spreadRadius: 2,
          ),
        ],
      ),
      child: Text(
        _message,
        style: const TextStyle(
          color: Colors.black87,
          fontSize: 14,
        ),
      ),
    );
  }
}
