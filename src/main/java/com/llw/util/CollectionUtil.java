package com.llw.util;

import com.google.common.collect.ImmutableMap;

/**
 * @discription: 集合工具类
 * @author: llw
 * @date: 2018-11-16
 */
public class CollectionUtil {

    /**
     * 获取通用map集合
     * @return 通用map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<Object, Object> generalMap() throws Exception {

        return ImmutableMap.builder();
    }

    /**
     * 获取字段map集合, 用来模拟对象字段
     * @return 字段map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<String, Object> fieldMap() throws Exception {

        return ImmutableMap.builder();
    }

    /**
     * 获取字符串map集合
     * @return 字符串map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<String, String> stringMap() throws Exception {

        return ImmutableMap.builder();
    }

}
