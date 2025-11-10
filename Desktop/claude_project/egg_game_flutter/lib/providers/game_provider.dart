import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'dart:convert';
import '../models/egg_model.dart';
import '../models/shop_item.dart';

class GameProvider with ChangeNotifier {
  EggModel _egg = EggModel();
  int _coins = 100; // 시작 코인
  List<String> _ownedItems = []; // 소유한 아이템 ID들
  List<String> _decorations = []; // 설치된 장식품들
  
  EggModel get egg => _egg;
  int get coins => _coins;
  List<String> get ownedItems => _ownedItems;
  List<String> get decorations => _decorations;
  
  GameProvider() {
    _loadData();
  }
  
  // 데이터 로드
  Future<void> _loadData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      
      // 알 데이터 로드
      final eggData = prefs.getString('egg_data');
      if (eggData != null) {
        _egg = EggModel.fromJson(json.decode(eggData));
        _egg.updateStatus(); // 시간 경과 반영
      }
      
      // 코인 로드
      _coins = prefs.getInt('coins') ?? 100;
      
      // 아이템 로드
      final itemsData = prefs.getString('owned_items');
      if (itemsData != null) {
        _ownedItems = List<String>.from(json.decode(itemsData));
      }
      
      // 장식 로드
      final decoData = prefs.getString('decorations');
      if (decoData != null) {
        _decorations = List<String>.from(json.decode(decoData));
      }
      
      notifyListeners();
    } catch (e) {
      debugPrint('데이터 로드 오류: ');
    }
  }
  
  // 데이터 저장
  Future<void> _saveData() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      
      await prefs.setString('egg_data', json.encode(_egg.toJson()));
      await prefs.setInt('coins', _coins);
      await prefs.setString('owned_items', json.encode(_ownedItems));
      await prefs.setString('decorations', json.encode(_decorations));
    } catch (e) {
      debugPrint('데이터 저장 오류: ');
    }
  }
  
  // 알 먹이주기
  void feedEgg(int amount) {
    _egg.feed(amount);
    notifyListeners();
    _saveData();
  }
  
  // 알과 놀기
  void playWithEgg(int amount) {
    _egg.play(amount);
    notifyListeners();
    _saveData();
  }
  
  // 경험치 추가
  void addExperience(int exp) {
    _egg.addExperience(exp);
    notifyListeners();
    _saveData();
  }
  
  // 코인 추가
  void addCoins(int amount) {
    _coins += amount;
    notifyListeners();
    _saveData();
  }
  
  // 아이템 구매
  bool buyItem(ShopItem item) {
    if (_coins >= item.price) {
      _coins -= item.price;
      _ownedItems.add(item.id);
      notifyListeners();
      _saveData();
      return true;
    }
    return false;
  }
  
  // 아이템 사용
  void useItem(ShopItem item) {
    if (_ownedItems.contains(item.id)) {
      switch (item.type) {
        case ItemType.food:
          feedEgg(item.effectValue);
          _ownedItems.remove(item.id);
          break;
        case ItemType.toy:
          playWithEgg(item.effectValue);
          _ownedItems.remove(item.id);
          break;
        case ItemType.decoration:
          if (!_decorations.contains(item.id)) {
            _decorations.add(item.id);
          }
          break;
      }
      notifyListeners();
      _saveData();
    }
  }
  
  // 장식 제거
  void removeDecoration(String itemId) {
    _decorations.remove(itemId);
    notifyListeners();
    _saveData();
  }
  
  // 미니게임 완료 보상
  void completeMinigame(int score) {
    final reward = score ~/ 10; // 점수의 1/10만큼 코인
    addCoins(reward);
    addExperience(score ~/ 5); // 점수의 1/5만큼 경험치
  }
}
