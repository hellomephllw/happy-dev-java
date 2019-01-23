package com.llw.express.persist.mysql.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * @description: 属性强行执行处理器
 * @author: llw
 * @date: 2018-11-24
 */
public class FieldForcer implements IFieldProcessor {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(FieldForcer.class);

    @Override
    public void byteField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "byte", true, true, false, false);
    }

    @Override
    public void shortField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "short", true, true, false, false);
    }

    @Override
    public void integerField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "int", true, true, false, false);
    }

    @Override
    public void longField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "bigint", true, true, false, false);
    }

    @Override
    public void floatField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "float", true, true, false, false);
    }

    @Override
    public void doubleField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "double", true, true, false, false);
    }

    @Override
    public void booleanField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "bit", true, false, false, false);
    }

    @Override
    public void stringField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "string", true, true, true, false);
    }

    @Override
    public void dateField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "date", true, true, false, false);
    }

    @Override
    public void bigDecimalField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "decimal", true, true, false, true);
    }

    /**
     * 通用检查
     * @param tableName 表名
     * @param entityFieldName 数据库字段名
     * @param field 实体字段
     * @param columnSet 数据库字段
     * @param dbFieldType 数据库字段类型
     * @param checkNullable 非空检查
     * @param checkUnique 唯一检查
     * @param checkLength 字符串长度检查
     * @param checkDecimal decimal精度检查
     * @throws Exception
     */
    private void genericForcer(String tableName,
                                String entityFieldName,
                                Field field,
                                ResultSet columnSet,
                                String dbFieldType,
                                boolean checkNullable,
                                boolean checkUnique,
                                boolean checkLength,
                                boolean checkDecimal) throws Exception {
        //构建参数
        FieldStateParams fieldStateParams = FieldStateParams.build(tableName, entityFieldName, field, columnSet, dbFieldType, checkNullable, checkUnique, checkLength, checkDecimal);

        //添加索引
        if (fieldStateParams.addUnique) {
            DatabaseHelper.addUniqueIndex(tableName, field);
            if (!fieldStateParams.modifyType
                    && !fieldStateParams.modifyLength
                    && !fieldStateParams.modifyBigDecimal
                    && fieldStateParams.canOwnUnique
                    && fieldStateParams.addNotNull) {
                logger.warn("【非常重要, 请注意】如果该字段为not null unique, 则忽略not null, 不然无法成功添加字段");
            }
        }
        //删除索引
        if (fieldStateParams.deleteUnique) {
            DatabaseHelper.deleteUniqueIndex(tableName, field);
        }
        //修改字段
        if (fieldStateParams.modifyType
                || fieldStateParams.modifyLength
                || fieldStateParams.modifyBigDecimal
                || fieldStateParams.addNotNull
                || fieldStateParams.deleteNotNull) {
            if (!(!fieldStateParams.modifyType
                    && !fieldStateParams.modifyLength
                    && !fieldStateParams.modifyBigDecimal
                    && fieldStateParams.canOwnUnique
                    && fieldStateParams.addNotNull)) {
                DatabaseHelper.modifyField(tableName, field);
            }
        }
    }

}
