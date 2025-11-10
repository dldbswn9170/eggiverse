class EggModel {
  int level;
  int experience;
  int hunger;
  int happiness;
  DateTime lastFeedTime;
  DateTime birthTime;
  
  EggModel({
    this.level = 1,
    this.experience = 0,
    this.hunger = 100,
    this.happiness = 100,
    DateTime? lastFeedTime,
    DateTime? birthTime,
  }) : lastFeedTime = lastFeedTime ?? DateTime.now(),
       birthTime = birthTime ?? DateTime.now();
  
  // 경험치 추가
  void addExperience(int exp) {
    experience += exp;
    while (experience >= getRequiredExp()) {
      levelUp();
    }
  }
  
  // 레벨업에 필요한 경험치
  int getRequiredExp() {
    return level * 100;
  }
  
  // 레벨업
  void levelUp() {
    experience -= getRequiredExp();
    level++;
  }
  
  // 먹이주기
  void feed(int amount) {
    hunger = (hunger + amount).clamp(0, 100);
    happiness = (happiness + 5).clamp(0, 100);
    lastFeedTime = DateTime.now();
  }
  
  // 놀아주기
  void play(int amount) {
    happiness = (happiness + amount).clamp(0, 100);
    hunger = (hunger - 5).clamp(0, 100);
  }
  
  // 시간 경과에 따른 상태 업데이트
  void updateStatus() {
    final now = DateTime.now();
    final hoursPassed = now.difference(lastFeedTime).inHours;
    
    hunger = (hunger - hoursPassed * 5).clamp(0, 100);
    happiness = (happiness - hoursPassed * 3).clamp(0, 100);
  }
  
  // JSON 변환
  Map<String, dynamic> toJson() {
    return {
      'level': level,
      'experience': experience,
      'hunger': hunger,
      'happiness': happiness,
      'lastFeedTime': lastFeedTime.toIso8601String(),
      'birthTime': birthTime.toIso8601String(),
    };
  }
  
  factory EggModel.fromJson(Map<String, dynamic> json) {
    return EggModel(
      level: json['level'],
      experience: json['experience'],
      hunger: json['hunger'],
      happiness: json['happiness'],
      lastFeedTime: DateTime.parse(json['lastFeedTime']),
      birthTime: DateTime.parse(json['birthTime']),
    );
  }
}
