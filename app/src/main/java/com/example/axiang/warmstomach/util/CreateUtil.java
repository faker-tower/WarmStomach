package com.example.axiang.warmstomach.util;

import java.util.Random;

/**
 * Created by a2389 on 2017/12/15.
 */

public class CreateUtil {

    // 根据指定长度生成字母和数字的随机数
    public static String createUserName(int length) {
        if (length <= 0) {
            return null;
        }
        StringBuilder userName = new StringBuilder();
        Random random = new Random();
        Random randomData = new Random();
        for (int i = 0; i < length; i++) {
            int index = randomData.nextInt(3);
            switch (index) {
                case 0:
                    userName.append(random.nextInt(10));
                    break;
                case 1:
                case 2:
                    userName.append((char)(random.nextInt(26) + 97));
                    break;
            }
        }
        return userName.toString();
    }
}
