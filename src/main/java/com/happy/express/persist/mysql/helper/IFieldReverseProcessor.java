package com.happy.express.persist.mysql.helper;

/**
 * @description: 通过数据库到实体的方向处理数据库
 * @author: happy
 * @date: 2018-11-24
 */
public interface IFieldReverseProcessor {

    public void unusedField(String tableName, String dbFieldName) throws Exception;

    public void unusedUniqueIndex(String tableName, String uniqueIndexName) throws Exception;

}
