package com.happy.util;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.annotation.Annotation;

/**
 * @description: 反射工具类
 * @author: llw
 * @date: 2019-05-24
 */
public class ReflectionUtil {

    /**
     * 获取表名
     * @param entity 实体类模板
     * @return 表名
     * @throws Exception
     */
    public static String getTableName(Class entity) throws Exception {
        String tableName = null;
        for (Annotation annotation : entity.getAnnotations()) {
            if (annotation.annotationType() == Table.class) {
                tableName = ((Table) annotation).name().toLowerCase();
                break;
            }
        }

        return tableName;
    }

    /**
     * 判断是否是实体
     * @param entity 实体类模板
     * @return 是否是实体
     * @throws Exception
     */
    public static boolean isEntity(Class entity) throws Exception {
        boolean isEntity = false;
        for (Annotation annotation : entity.getAnnotations()) {
            if (annotation.annotationType() == Entity.class) {
                isEntity = true;
                break;
            }
        }

        return isEntity;
    }

}
