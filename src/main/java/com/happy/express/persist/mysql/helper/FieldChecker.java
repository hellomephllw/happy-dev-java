package com.happy.express.persist.mysql.helper;

import com.happy.express.persist.annotation.HappyCol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
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
    public void byteField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "byte",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void shortField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "short",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void integerField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "int",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void longField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "bigint",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void floatField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "float",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void doubleField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "double",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void booleanField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "bit",
                true,
                false,
                false,
                true,
                false,
                false);
    }

    @Override
    public void stringField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "string",
                true,
                true,
                false,
                true,
                false,
                false);
    }

    @Override
    public void dateField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "date",
                true,
                false,
                false,
                true,
                true,
                true);
    }

    @Override
    public void bigDecimalField(String tableName, Field field, ResultSet columnSet) throws Exception {
        String entityFieldName = field.getName();

        genericChecker(
                tableName,
                entityFieldName,
                field,
                columnSet,
                "decimal",
                true,
                false,
                true,
                true,
                false,
                false);
    }

    /**
     * 通用检查
     * @param tableName 表名
     * @param entityFieldName 数据库字段名
     * @param field 实体字段
     * @param columnSet 数据库字段
     * @param checkNullable 非空检查
     * @param checkLength 字符串长度检查
     * @param checkDecimal decimal精度检查
     * @param checkDefault 默认值检查
     * @param checkCreateTime 创建时间检查
     * @param checkUpdateTime 更新时间检查
     * @throws Exception
     */
    private void genericChecker(String tableName,
                                String entityFieldName,
                                Field field,
                                ResultSet columnSet,
                                String dbFieldType,
                                boolean checkNullable,
                                boolean checkLength,
                                boolean checkDecimal,
                                boolean checkDefault,
                                boolean checkCreateTime,
                                boolean checkUpdateTime) throws Exception {
        //构建参数
        FieldStateParams fieldStateParams = FieldStateParams.build(tableName, entityFieldName, field, columnSet, dbFieldType, checkNullable, checkLength, checkDecimal, checkDefault, checkCreateTime, checkUpdateTime);

        String dbFieldName = DatabaseHelper.getDatabaseFieldName(entityFieldName);
        //修改字段
        Column column = field.getAnnotation(Column.class);
        HappyCol happyCol = field.getAnnotation(HappyCol.class);
        if (fieldStateParams.modifyType
                || fieldStateParams.modifyLength
                || fieldStateParams.modifyBigDecimal
                || fieldStateParams.addNotNull
                || fieldStateParams.deleteNotNull
                || fieldStateParams.addDefault
                || fieldStateParams.deleteDefault
                || fieldStateParams.addCreateTime
                || fieldStateParams.deleteCreateTime
                || fieldStateParams.addUpdateTime
                || fieldStateParams.deleteUpdateTime) {
            //decimal
            if (fieldStateParams.modifyBigDecimal) {
                int entityFieldPrecision = 0;
                int entityFieldScale = 0;
                int dbFieldPrecision = 0;
                int dbFieldScale = 0;
                if (column != null) {
                    entityFieldPrecision = column.precision();
                    entityFieldScale = column.scale();
                    dbFieldPrecision = columnSet.getInt("COLUMN_SIZE");
                    dbFieldScale = columnSet.getInt("DECIMAL_DIGITS");
                } else if (happyCol != null) {
                    entityFieldPrecision = happyCol.precision();
                    entityFieldScale = happyCol.scale();
                    dbFieldPrecision = columnSet.getInt("COLUMN_SIZE");
                    dbFieldScale = columnSet.getInt("DECIMAL_DIGITS");
                }
                if (entityFieldPrecision != dbFieldPrecision) {
                    logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")最大长度为(" + dbFieldPrecision + "), 需要变为" + entityFieldPrecision);
                }
                if (entityFieldScale != dbFieldScale) {
                    logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")小数位数为(" + dbFieldScale + "), 需要变为" + entityFieldScale);
                }
            }
            //length
            if (fieldStateParams.modifyLength) {
                int entityFieldLen = 0;
                int dbFieldLen = 0;
                if (column != null) {
                    entityFieldLen = column.length();
                    dbFieldLen = columnSet.getInt("COLUMN_SIZE");
                } else if (happyCol != null) {
                    entityFieldLen = happyCol.len();
                    dbFieldLen = columnSet.getInt("COLUMN_SIZE");
                }
                if (dbFieldLen != entityFieldLen) {
                    logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")字符串长度为(" + dbFieldLen + "), 需要变为" + entityFieldLen);
                }
            }
            //添加非空
            if (fieldStateParams.addNotNull) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")可为空, 需要变为不为空");
            }
            //删除非空
            if (fieldStateParams.deleteNotNull) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")不能为空, 需要变为可为空");
            }
            //类型
            if (fieldStateParams.modifyType) {
                String typeStr = columnSet.getString("TYPE_NAME");
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")的类型为(" + typeStr.toLowerCase() + "), 需要变为" + dbFieldType);
            }
            //添加默认值
            if (!fieldStateParams.addCreateTime && fieldStateParams.addDefault) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要添加默认值");
            }
            //删除默认值
            if (!fieldStateParams.deleteCreateTime && fieldStateParams.deleteDefault) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要删除默认值");
            }
            //添加createTime
            if (fieldStateParams.addCreateTime) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要添加createTime");
            }
            //删除createTime
            if (fieldStateParams.deleteCreateTime) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要删除createTime");
            }
            //添加updateTime
            if (fieldStateParams.addUpdateTime) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要添加updateTime");
            }
            //删除updateTime
            if (fieldStateParams.deleteUpdateTime) {
                logger.warn("数据库表(" + tableName + ")字段(" + dbFieldName + ")需要删除updateTime");
            }
        }

    }

}
