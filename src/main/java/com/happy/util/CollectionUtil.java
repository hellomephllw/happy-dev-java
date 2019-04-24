package com.happy.util;

import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.Map;

/**
 * @description: 集合工具类
 * @author: llw
 * @date: 2018-11-16
 */
public class CollectionUtil {

    /**
     * 获取通用map集合
     * @return 通用map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<Object, Object> generalMap() {

        return ImmutableMap.builder();
    }

    /**
     * 获取字段map集合, 用来模拟对象字段
     * @return 字段map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<String, Object> fieldMap() {

        return ImmutableMap.builder();
    }

    /**
     * 获取字符串map集合
     * @return 字符串map集合
     * @throws Exception
     */
    public static ImmutableMap.Builder<String, String> stringMap() {

        return ImmutableMap.builder();
    }

    /**
     * 集合是空
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 键值对是空
     * @param map 键值对
     * @return 是否为空
     */
    public static boolean mapIsEmpty(Map map) {
        return map == null || map.isEmpty();
    }

}
