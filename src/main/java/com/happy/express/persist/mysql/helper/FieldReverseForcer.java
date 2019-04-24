package com.happy.express.persist.mysql.helper;

/**
 * @description: 通过数据库到实体的方向处理数据库检查器
 * @author: llw
 * @date: 2018-11-27
 */
public class FieldReverseForcer implements IFieldReverseProcessor {

    @Override
    public void unusedField(String tableName, String dbFieldName) throws Exception {
        DatabaseHelper.deleteField(tableName, dbFieldName);
    }

    @Override
    public void unusedUniqueIndex(String tableName, String uniqueIndexName) throws Exception {
        DatabaseHelper.deleteUniqueIndex(tableName, uniqueIndexName);
    }

}
