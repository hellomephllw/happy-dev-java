package com.llw.util;

import java.util.Random;

/**
 * @discription: 数字工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class NumberUtil {

    /**
     * 生成随机整数
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     * @throws Exception
     */
    public static long generateRandomIntegerNumber(int min, int max) throws Exception {
        Random random = new Random();

        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 生成随机整数
     * @param count 位数
     * @return 随机整数
     * @throws Exception
     */
    public static long generateRandomIntegerNumber(int count) throws Exception {
        long result = (long) (Math.random() * Math.pow(10, count));

        if (result < Math.pow(10, count - 1)) {
            result = NumberUtil.generateRandomIntegerNumber(count);
        }

        return result;
    }

}
