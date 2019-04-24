package com.happy.util;

/**
 * @description: 字符串工具类
 * @author: llw
 * @date: 2019-03-11
 */
public class StringUtil {

    /**
     * 非空验证
     * @param str 字符串
     * @return 是否是空
     */
    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

}
