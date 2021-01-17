package com.happy.express.persist.mysql.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 通过数据库到实体的方向处理数据库检查器
 * @author: llw
 * @date: 2018-11-27
 */
public class FieldReverseChecker implements IFieldReverseProcessor {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(FieldReverseChecker.class);

    @Override
    public void unusedField(String tableName, String dbFieldName) throws Exception {
        logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")在实体中不存在");
    }

    @Override
    public void unusedUniqueIndex(String tableName, String uniqueIndexName) throws Exception {
        logger.warn("数据库表(" + tableName + ")唯一索引(" + uniqueIndexName + ")是多余的, 需要删除");
    }

    @Override
    public void unusedIndex(String tableName, String indexName) throws Exception {
        logger.warn("数据库表(" + tableName + ")索引(" + indexName + ")是多余的, 需要删除");
    }
}
