package com.eggiverse.app.util;

import com.eggiverse.app.R;

/**
 * 게이머의 레벨에 따른 캐릭터 drawable ID를 반환하는 유틸리티 클래스
 */
public class CharacterDrawableUtil {

    /**
     * 현재 레벨에 따른 캐릭터 drawable 리소스 ID를 반환합니다.
     * Level 1: pixel_egg.png (초기 알)
     * Level 2: level1.png (첫 번째 진화)
     * Level 3+: level2.png (두 번째 진화)
     *
     * @param level 현재 레벨
     * @return 캐릭터 drawable 리소스 ID
     */
    public static int getCharacterDrawableId(int level) {
        if (level >= 3) {
            return R.drawable.level2;
        } else if (level >= 2) {
            return R.drawable.level1;
        } else {
            return R.drawable.pixel_egg;
        }
    }
}
