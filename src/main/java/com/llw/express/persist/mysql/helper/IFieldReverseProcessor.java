package com.llw.express.persist.mysql.helper;

import java.lang.reflect.Field;

/**
 * @description: 通过数据库处理数据库
 * @author: llw
 * @date: 2018-11-24
 */
public interface IFieldReverseProcessor {

    public void unusedField(String tableName, Field entityField) throws Exception;

    public void unusedUniqueIndex(String tableName, Field entityField) throws Exception;

}
