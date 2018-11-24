package com.llw.express.persist.mysql.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * @description: 属性检查器(正向, 通过实体属性处理数据库表)
 * @author: llw
 * @date: 2018-11-24
 */
public class FieldChecker implements IFieldProcessor {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(FieldChecker.class);

    @Override
    public void integerField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型检查
        fieldTypeChecker(tableName, dbFieldName, columnSet, "int");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, false);
    }

    @Override
    public void longField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "bigint");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, false);
    }

    @Override
    public void floatField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "float");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, false);
    }

    @Override
    public void doubleField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "double");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, false);
    }

    @Override
    public void booleanField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "int");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, false, false, false);
    }

    @Override
    public void stringField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "varchar");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, true, false);
    }

    @Override
    public void dateField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "timestamp");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, false);
    }

    @Override
    public void bigDecimalField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //字段类型判断
        fieldTypeChecker(tableName, dbFieldName, columnSet, "decimal");
        //字段的其他检查
        genericChecker(tableName, entityFieldName, field, columnSet, true, true, false, true);
    }

    /**
     * 字段类型检查
     * @param tableName 表名
     * @param dbFieldName 数据库字段名
     * @param columnSet 数据库字段
     * @param dbFieldType 数据库字段类型
     * @throws Exception
     */
    private void fieldTypeChecker(String tableName, String dbFieldName, ResultSet columnSet, String dbFieldType) throws Exception {
        dbFieldType = dbFieldType.toLowerCase();
        String typeStr = columnSet.getString("TYPE_NAME");
        if (!(typeStr != null && dbFieldType.equals(typeStr.toLowerCase()))) {
            logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")的类型为(" + typeStr.toLowerCase() + "), 需要变为" + dbFieldType);
        }
    }

    /**
     * 通用检查
     * @param tableName 表名
     * @param entityFieldName 数据库字段名
     * @param field 实体字段
     * @param columnSet 数据库字段
     * @param checkNullable 非空检查
     * @param checkUnique 唯一检查
     * @param checkLength 字符串长度检查
     * @param checkDecimal decimal精度检查
     * @throws Exception
     */
    private void genericChecker(String tableName,
                                String entityFieldName,
                                Field field,
                                ResultSet columnSet,
                                boolean checkNullable,
                                boolean checkUnique,
                                boolean checkLength,
                                boolean checkDecimal) throws Exception {
        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);

        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType() == Column.class) {
                if (checkNullable) {
                    //非空检查
                    int nullable = columnSet.getInt("NULLABLE");
                    if (((Column) annotation).nullable()) {
                        if (nullable == 0) {
                            logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")不能为空, 需要变为可为空");
                        }
                    } else {
                        if (nullable == 1) {
                            logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")可为空, 需要变为不为空");
                        }
                    }
                }
                if (checkUnique) {
                    //唯一索引检查
                    if (((Column) annotation).unique()) {
                        if (!DatabaseHelper.existUniqueIndex(tableName, entityFieldName)) {
                            logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")有唯一索引, 需要为该字段添加唯一索引");
                        }
                    } else {
                        if (DatabaseHelper.existUniqueIndex(tableName, entityFieldName)) {
                            logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")没有唯一索引, 需要删除该唯一索引(" + DatabaseHelper.getUniqueIndexName(tableName, entityFieldName) + ")");
                        }
                    }
                }
                if (checkLength) {
                    //字符串长度检查
                    int entityFieldLen = ((Column) annotation).length();
                    int dbFieldLen = columnSet.getInt("COLUMN_SIZE");
                    if (dbFieldLen != entityFieldLen) {
                        logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")字符串长度为(" + dbFieldLen + "), 需要变为" + entityFieldLen);
                    }
                }
                if (checkDecimal) {
                    //bigDecimal检查
                    int entityFieldPrecision = ((Column) annotation).precision();
                    int entityFieldScale = ((Column) annotation).scale();
                    int dbFieldPrecision = columnSet.getInt("COLUMN_SIZE");
                    int dbFieldScale = columnSet.getInt("DECIMAL_DIGITS");
                    if (entityFieldPrecision != dbFieldPrecision) {
                        logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")最大长度为(" + dbFieldPrecision + "), 需要变为" + entityFieldPrecision);
                    }
                    if (entityFieldScale != dbFieldScale) {
                        logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")小数位数为(" + dbFieldScale + "), 需要变为" + entityFieldScale);
                    }
                }
                break;
            }
        }
    }

}
