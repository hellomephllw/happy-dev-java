package com.happy.express.persist.mysql.helper;

import com.happy.express.persist.annotation.HappyCol;

import javax.persistence.Column;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * @description: 字段的情况
 * @author: llw
 * @date: 2018-11-27
 */
public class FieldStateParams {

    /**
     * 单例
     */
    public final static FieldStateParams fieldStateParams = new FieldStateParams();

    /**
     * 是否要修改类型
     */
    public boolean modifyType = false;
    /**
     * 是否修改字符串长度
     */
    public boolean modifyLength = false;
    /**
     * 是否修改bigDecimal的最大长度和小数位数
     */
    public boolean modifyBigDecimal = false;
    /**
     * 是否要添加非空
     */
    public boolean addNotNull = false;
    /**
     * 是否要删除非空
     */
    public boolean deleteNotNull = false;

    /**
     * 构建参数
     * @param tableName       表名
     * @param entityFieldName 实体字段名
     * @param field           字段
     * @param columnSet       数据库字段
     * @param dbFieldType     数据库字段类型
     * @param checkNullable   检查非空
     * @param checkLength     检查字符长度
     * @param checkDecimal    检查decimal
     * @return 实体字段情况
     * @throws Exception
     */
    public static FieldStateParams build(String tableName,
                                         String entityFieldName,
                                         Field field,
                                         ResultSet columnSet,
                                         String dbFieldType,
                                         boolean checkNullable,
                                         boolean checkLength,
                                         boolean checkDecimal) throws Exception {
        //重置属性
        reset();

        //获取注解信息
        Column column = field.getAnnotation(Column.class);
        HappyCol happyCol = field.getAnnotation(HappyCol.class);
        int entityFieldLen = 0;
        int entityFieldPrecision = 0;
        int entityFieldScale = 0;
        boolean isChar = false;
        boolean isText = false;
        boolean isLongText = false;
        boolean isNullable = false;
        if (column != null) {
            entityFieldLen = column.length();
            entityFieldPrecision = column.precision();
            entityFieldScale = column.scale();
            isNullable = column.nullable();
        } else if (happyCol != null) {
            entityFieldLen = happyCol.len();
            entityFieldPrecision = happyCol.precision();
            entityFieldScale = happyCol.scale();
            isChar = happyCol.fixLen() && entityFieldLen > 0;
            isText = happyCol.text();
            isLongText = happyCol.longText();
            isNullable = happyCol.nullable();
        }

        boolean isDate = false;
        //是否要修改类型
        if ("string".equals(dbFieldType.toLowerCase())) {
            String typeStr = columnSet.getString("TYPE_NAME");
            if (("char".equals(typeStr.toLowerCase()) && !isChar)
                    || ("text".equals(typeStr.toLowerCase()) && !isText)
                    || ("longtext".equals(typeStr.toLowerCase()) && !isLongText)
                    || ("varchar".equals(typeStr.toLowerCase()) && (entityFieldLen <= 0 || isChar))) {
                fieldStateParams.modifyType = true;
            }
        } else if ("byte".equals(dbFieldType.toLowerCase())) {
            String typeStr = columnSet.getString("TYPE_NAME");
            if (!"tinyint".equals(typeStr.toLowerCase())) {
                fieldStateParams.modifyType = true;
            }
        } else if ("short".equals(dbFieldType.toLowerCase())) {
            String typeStr = columnSet.getString("TYPE_NAME");
            if (!"smallint".equals(typeStr.toLowerCase())) {
                fieldStateParams.modifyType = true;
            }
        } else if ("date".equals(dbFieldType.toLowerCase())) {
            isDate = true;
            String typeStr = columnSet.getString("TYPE_NAME");
            if (!"timestamp".equals(typeStr.toLowerCase())) {
                fieldStateParams.modifyType = true;
            }
        } else {
            fieldStateParams.modifyType = !fieldTypeChecker(columnSet, dbFieldType);
        }

        if (checkNullable && !isDate) {
            //非空检查
            int nullable = columnSet.getInt("NULLABLE");
            if (isNullable) {
                if (nullable == 0) {
                    fieldStateParams.deleteNotNull = true;
                }
            } else {//不可为空
                if (nullable == 1) {
                    fieldStateParams.addNotNull = true;
                }
            }
        }
        if (checkLength) {
            //字符串长度检查
            String typeStr = columnSet.getString("TYPE_NAME");
            if (typeStr.toLowerCase().equals("varchar")) {
                int dbFieldLen = columnSet.getInt("COLUMN_SIZE");
                if (dbFieldLen != entityFieldLen) {
                    fieldStateParams.modifyLength = true;
                }
            }
        }
        if (checkDecimal) {
            //bigDecimal检查
            int dbFieldPrecision = columnSet.getInt("COLUMN_SIZE");
            int dbFieldScale = columnSet.getInt("DECIMAL_DIGITS");
            if (entityFieldPrecision != dbFieldPrecision) {
                fieldStateParams.modifyBigDecimal = true;
            }
            if (entityFieldScale != dbFieldScale) {
                fieldStateParams.modifyBigDecimal = true;
            }
        }

        return fieldStateParams;
    }

    /**
     * 字段类型检查
     * @param columnSet   数据库字段
     * @param dbFieldType 数据库字段类型
     * @return boolean 是否通过
     * @throws Exception
     */
    private static boolean fieldTypeChecker(ResultSet columnSet, String dbFieldType) throws Exception {
        dbFieldType = dbFieldType.toLowerCase();
        String typeStr = columnSet.getString("TYPE_NAME");
        if (dbFieldType.equals(typeStr.toLowerCase())) {
            return true;
        }

        return false;
    }

    private static void reset() {
        fieldStateParams.modifyType = false;
        fieldStateParams.modifyLength = false;
        fieldStateParams.modifyBigDecimal = false;
        fieldStateParams.addNotNull = false;
        fieldStateParams.deleteNotNull = false;
    }

    @Override
    public String toString() {
        return "FieldStateParams{" +
                "modifyType=" + modifyType +
                ", modifyLength=" + modifyLength +
                ", modifyBigDecimal=" + modifyBigDecimal +
                ", addNotNull=" + addNotNull +
                ", deleteNotNull=" + deleteNotNull +
                '}';
    }

}
