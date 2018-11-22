package com.llw.util;

import java.util.Date;
import java.util.UUID;

/**
 * @description: 键生成工具类
 * @author: llw
 * @date: 2018-11-06
 */
public class KeyGenerateUtil {

    /**
     * 获取uuid
     * @return uuid
     * @throws Exception
     */
    public static String getUuidKey() throws Exception {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取时间戳
     * @return 时间戳
     * @throws Exception
     */
    public static String getTimestampKey() throws Exception {
        return "" + new Date().getTime();
    }

    /**
     * 获取时间戳-uuid
     * @return 时间戳-uuid
     * @throws Exception
     */
    public static String getTimestampWithUuidKey() throws Exception {
        return getTimestampKey() + "-" + KeyGenerateUtil.getUuidKey();
    }

    /**
     * 获取指定位数的随机数
     * @param count 指定的位数
     * @return 随机数
     * @throws Exception
     */
    public static String getRandomKey(int count) throws Exception {
        return "" + NumberUtil.generateRandomIntegerNumber(count);
    }

    /**
     * 获取随机数-时间戳
     * @param count 随机数指定位数
     * @return 随机数-时间戳
     * @throws Exception
     */
    public static String getRandomKeyWithTimestamp(int count) throws Exception {
        return getRandomKey(count) + "-" + getTimestampKey();
    }

}
