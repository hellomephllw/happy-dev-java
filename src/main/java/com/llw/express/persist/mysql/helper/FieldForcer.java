package com.llw.express.persist.mysql.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
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

        genericForcer(tableName, entityFieldName, field, columnSet, "int", true, false, false, false);
    }

    @Override
    public void stringField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "string", true, true, true, false);
    }

    @Override
    public void dateField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "timestamp", true, true, false, false);
    }

    @Override
    public void bigDecimalField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericForcer(tableName, entityFieldName, field, columnSet, "decimal", true, true, false, true);
    }

    /**
     * 字段类型检查
     * @param columnSet 数据库字段
     * @param dbFieldType 数据库字段类型
     * @return boolean 是否通过
     * @throws Exception
     */
    private boolean fieldTypeChecker(ResultSet columnSet, String dbFieldType) throws Exception {
        dbFieldType = dbFieldType.toLowerCase();
        String typeStr = columnSet.getString("TYPE_NAME");
        if (dbFieldType.equals(typeStr.toLowerCase())) {
            return true;
        }

        return false;
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
        //是否要修改类型
        boolean modifyType = false;
        if ("string".equals(dbFieldType.toLowerCase())) {
            String typeStr = columnSet.getString("TYPE_NAME");
            if (!("varchar".equals(typeStr.toLowerCase())
                    || "text".equals(typeStr.toLowerCase())
                    || "mediumtext".equals(typeStr.toLowerCase())
                    || "longtext".equals(typeStr.toLowerCase()))) {
                modifyType = true;
            }
        } else {
            modifyType = !fieldTypeChecker(columnSet, dbFieldType);
        }
        //是否修改字符串长度
        boolean modifyLength = false;
        //是否修改bigDecimal的最大长度和小数位数
        boolean modifyBigDecimal = false;
        //是否要添加非空
        boolean addNotNull = false;
        //是否要删除非空
        boolean deleteNotNull = false;
        //是否要添加唯一索引
        boolean addUnique = false;
        //是否要删除唯一索引
        boolean deleteUnique = false;

        //是否能够有唯一索引
        boolean canOwnUnique = false;

        for (Annotation annotation : field.getAnnotations()) {
            if (annotation.annotationType() == Column.class) {
                if (checkNullable) {
                    //非空检查
                    int nullable = columnSet.getInt("NULLABLE");
                    if (((Column) annotation).nullable()) {
                        if (nullable == 0) {
                            deleteNotNull = true;
                        }
                    } else {//不可为空
                        if (nullable == 1) {
                            addNotNull = true;
                        }
                    }
                }
                if (checkUnique) {
                    //唯一索引检查
                    if (((Column) annotation).unique()) {//唯一索引
                        canOwnUnique = true;
                        if (!DatabaseHelper.existUniqueIndex(tableName, entityFieldName)) {
                            addUnique = true;
                        }
                    } else {
                        if (DatabaseHelper.existUniqueIndex(tableName, entityFieldName)) {
                            deleteUnique = true;
                        }
                    }
                }
                if (checkLength) {
                    //字符串长度检查
                    String typeStr = columnSet.getString("TYPE_NAME");
                    if (typeStr.toLowerCase().equals("varchar")) {
                        int entityFieldLen = ((Column) annotation).length();
                        int dbFieldLen = columnSet.getInt("COLUMN_SIZE");
                        if (dbFieldLen != entityFieldLen) {
                            modifyLength = true;
                        }
                    }
                }
                if (checkDecimal) {
                    //bigDecimal检查
                    int entityFieldPrecision = ((Column) annotation).precision();
                    int entityFieldScale = ((Column) annotation).scale();
                    int dbFieldPrecision = columnSet.getInt("COLUMN_SIZE");
                    int dbFieldScale = columnSet.getInt("DECIMAL_DIGITS");
                    if (entityFieldPrecision != dbFieldPrecision) {
                        modifyBigDecimal = true;
                    }
                    if (entityFieldScale != dbFieldScale) {
                        modifyBigDecimal = true;
                    }
                }
                break;
            }
        }

        //添加索引
        if (addUnique) {
            DatabaseHelper.addUniqueIndex(tableName, field);
            if (!modifyType && !modifyLength && !modifyBigDecimal && canOwnUnique && addNotNull) {
                logger.warn("【非常重要, 请注意】如果该字段为not null unique, 则忽略not null, 不然无法成功添加字段");
            }
        }
        //删除索引
        if (deleteUnique) {
            DatabaseHelper.deleteUniqueIndex(tableName, field);
        }
        //修改字段
        if (modifyType || modifyLength || modifyBigDecimal || addNotNull || deleteNotNull) {
            if (!(!modifyType && !modifyLength && !modifyBigDecimal && canOwnUnique && addNotNull)) {
                DatabaseHelper.modifyField(tableName, field);
            }
        }
    }

}
