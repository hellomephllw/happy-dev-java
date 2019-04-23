package com.happy.util;

import java.util.Date;
import java.util.UUID;

/**
 * @description: 键生成工具类
 * @author: happy
 * @date: 2018-11-06
 */
public class KeyGenerateUtil {

    /**
     * 获取uuid
     * @return uuid
     * @throws Exception
     */
    public static String getUuidKey() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取时间戳
     * @return 时间戳
     * @throws Exception
     */
    public static String getTimestampKey() {
        return "" + new Date().getTime();
    }

    /**
     * 获取时间戳-uuid
     * @return 时间戳-uuid
     * @throws Exception
     */
    public static String getTimestampWithUuidKey() {
        return getTimestampKey() + "-" + KeyGenerateUtil.getUuidKey();
    }

    /**
     * 获取指定位数的随机数
     * @param count 随机数长度
     * @return 随机数
     * @throws Exception
     */
    public static String getRandomKey(int count) {
        return "" + NumberUtil.generateRandomIntegerNumber(count);
    }

    /**
     * 获取随机数-时间戳
     * @param count 随机数长度
     * @return 随机数-时间戳
     * @throws Exception
     */
    public static String getRandomWithTimestampKey(int count) {
        return getRandomKey(count) + "-" + getTimestampKey();
    }

    /**
     * 获取随机数-uuid
     * @param count 随机数长度
     * @return
     */
    public static String getRandomWithUuidKey(int count) {
        return getRandomKey(count) + "-" + getUuidKey();
    }

}
