package com.happy.express.persist.mysql.helper;

import com.happy.express.persist.annotation.HappyCol;
import com.happy.express.persist.annotation.HappyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.TableGenerator;
import javax.persistence.Version;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 数据库处理工具
 * @author: llw
 * @date: 2018-11-23
 */
public class DatabaseHelper extends BaseDatabaseHelper {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    //=======================操作数据库
    /**
     * 创建数据库表格
     * @param tableName 表名
     * @param entityFields 实体所有属性
     * @throws Exception
     */
    public static void addTable(String tableName, List<Field> entityFields) throws Exception {
        //建表sql
        StringBuilder sql = new StringBuilder("create table " + tableName + "(");
        for (int i = 0; i < entityFields.size(); ++i) {
            Field entityField = entityFields.get(i);
            String fieldLine = createFieldStringForAddTable(entityField);
            if ("".equals(fieldLine)) continue;
            sql.append(fieldLine);
            if (i == entityFields.size() - 1) break;
            sql.append(",");
        }
        sql.append(")");

        //添加id起始值
        sql.append(addIdInitialValue(entityFields));

        //执行建表
        statement.executeUpdate(sql.toString());

        logger.info("已创建数据库表: " + tableName);
        logger.info("建表sql: " + sql.toString());
    }

    /**
     * 删除表格
     * @param tableName 表名
     * @throws Exception
     */
    public static void deleteTable(String tableName) throws Exception {
        //删表sql
        String sql = "drop table " + tableName + ";";
        sql = "set foreign_key_checks = 0; " + sql;

        //执行删除
        statement.executeUpdate(sql);

        logger.info("已删除数据库表: " + tableName);
        logger.info("删除表格sql: " + sql);
    }

    /**
     * 添加数据库表字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void addField(String tableName, Field entityField) throws Exception {
        Column column = entityField.getAnnotation(Column.class);
        HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
        if (column == null && happyCol == null) return ;

        boolean isNotNull = isNotNull(column, happyCol);
        //添加字段sql
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" add ");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(" ");
        sql.append(getWholeDbFieldTypeByEntityFieldType(entityField));
        if (isNotNull) sql.append(" not null");
        sql.append(";");

        //执行添加字段
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (isNotNull ? " not null" : "");
        logger.info("为数据库表(" + tableName + ")添加字段: " + fieldStr);
        logger.info("添加字段的sql: " + sql.toString());
    }

    /**
     * 修改数据库表字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void modifyField(String tableName, Field entityField) throws Exception {
        Column column = entityField.getAnnotation(Column.class);
        HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
        Id id = entityField.getAnnotation(Id.class);
        HappyId happyId = entityField.getAnnotation(HappyId.class);
        Version version = entityField.getAnnotation(Version.class);
        if (id != null || happyId != null) {
            modifyId(tableName, entityField);
            return ;
        }
        if (version != null) {
            modifyVersion(tableName, entityField);
            return ;
        }
        if (column == null && happyCol == null) return ;
        boolean isNotNull = isNotNull(column, happyCol);

        //修改sql字段
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" modify ");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(" ");
        sql.append(getWholeDbFieldTypeByEntityFieldType(entityField));
        if (isNotNull) {
            sql.append(" not null");
        }
        sql.append(";");

        //执行修改字段
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (isNotNull ? " not null" : "");
        logger.warn("把数据库表(" + tableName + ")字段(" + getDatabaseFieldName(entityField.getName()) + "), 修改为" + fieldStr);
        logger.warn("修改字段的sql: " + sql.toString());
    }

    /**
     * 修改id字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    private static void modifyId(String tableName, Field entityField) throws Exception {
        String sql = "alter table " + tableName + " modify id " + getWholeDbFieldTypeByEntityFieldType(entityField) + ";";

        //执行修改
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(id), 修改为id " + getWholeDbFieldTypeByEntityFieldType(entityField));
        logger.warn("修改id字段的sql: " + sql);
    }

    /**
     * 修改version字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    private static void modifyVersion(String tableName, Field entityField) throws Exception {
        String sql = "alter table " + tableName + " modify version " + getWholeDbFieldTypeByEntityFieldType(entityField) + ";";

        //执行修改
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(version), 修改为version " + getWholeDbFieldTypeByEntityFieldType(entityField));
        logger.warn("修改version字段的sql: " + sql);
    }

    /**
     * 删除字段
     * @param tableName 表名
     * @param dbFieldName 数据库字段名
     * @throws Exception
     */
    public static void deleteField(String tableName, String dbFieldName) throws Exception {
        String sql = "alter table " + tableName + " drop " + dbFieldName + ";";

        //执行删除
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(" + dbFieldName + ")删除");
        logger.warn("删除字段的sql: " + sql);
    }

    /**
     * 添加数据库字段唯一索引
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void addUniqueIndex(String tableName, Field entityField) throws Exception {
        Column column = entityField.getAnnotation(Column.class);
        HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
        if (column == null && happyCol == null) return ;
//        if ((column != null && !column.unique()) && (happyCol != null && !happyCol.unique())) return ;
        if (existUniqueIndex(tableName, entityField.getName())) return ;

        String uniqueIndexName = getUniqueIndexName(entityField.getName());
        StringBuilder sql = new StringBuilder();
        sql.append("create unique index");
        sql.append(" ");
        sql.append(uniqueIndexName);
        sql.append(" on ");
        sql.append(tableName);
        sql.append("(");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(");");

        statement.executeUpdate(sql.toString());

        logger.info("为数据库表(" + tableName + ")字段(" + getDatabaseFieldName(entityField.getName()) + ")添加唯一索引(" + uniqueIndexName + ")");
    }

    /**
     * 删除唯一索引
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void deleteUniqueIndex(String tableName, Field entityField) throws Exception {
        Column column = entityField.getAnnotation(Column.class);
        HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
        if (column == null && happyCol == null) return ;
//        if ((column != null && column.unique()) || (happyCol != null && happyCol.unique())) return ;
        if (!existUniqueIndex(tableName, entityField.getName())) return ;

        String uniqueIndexName = getUniqueIndexName(entityField.getName());
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" drop index ");
        sql.append(getUniqueIndexName(entityField.getName()));
        sql.append(";");

        statement.executeUpdate(sql.toString());

        logger.info("把数据库表(" + tableName + ")字段(" + getDatabaseFieldName(entityField.getName()) + ")的唯一索引(" + uniqueIndexName + ")删除");
    }

    /**
     * 删除唯一索引
     * @param tableName 表名
     * @param uniqueIndexName 索引名称
     * @throws Exception
     */
    public static void deleteUniqueIndex(String tableName, String uniqueIndexName) throws Exception {
        String sql = "alter table " + tableName + " drop index " + uniqueIndexName + ";";

        statement.executeUpdate(sql);

        logger.info("把数据库表(" + tableName + ")的唯一索引(" + uniqueIndexName + ")删除");
    }

    /**
     * 创建表字段字符串
     * @param entityField 实体字段
     * @return 表字段字符串
     * @throws Exception
     */
    private static String createFieldStringForAddTable(Field entityField) throws Exception {
        if (entityField.getAnnotation(Id.class) != null || entityField.getAnnotation(HappyId.class) != null) {
            return createPrimaryKeyStringForAddTable();
        }
        if (entityField.getAnnotation(Version.class) != null || entityField.getAnnotation(Version.class) != null) {
            return createVersionStringForAddTable();
        }
        if (entityField.getAnnotation(Column.class) != null || entityField.getAnnotation(HappyCol.class) != null) {
            Column column = entityField.getAnnotation(Column.class);
            HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
            StringBuilder fieldLine = new StringBuilder();
            fieldLine.append(" ");
            fieldLine.append(getDatabaseFieldName(entityField.getName()));
            fieldLine.append(" ");
            fieldLine.append(getWholeDbFieldTypeByEntityFieldType(entityField));
            fieldLine.append(" ");
            if (isNotNull(column, happyCol)) {
                fieldLine.append("not null");
            }
            return fieldLine.toString();
        }

        return "";
    }

    /**
     * 创建主键字段字符串
     * @return 主键字段字符串
     * @throws Exception
     */
    private static String createPrimaryKeyStringForAddTable() throws Exception {

        return "id int auto_increment primary key not null";
    }

    /**
     * 添加id起始值
     * @param fields 字段
     * @return 添加起始值语法
     * @throws Exception
     */
    private static String addIdInitialValue(List<Field> fields) throws Exception {
        for (Field field : fields) {
            Id id = field.getAnnotation(Id.class);
            TableGenerator tableGenerator = field.getAnnotation(TableGenerator.class);
            if (id != null && tableGenerator != null) {
                int initialValue = tableGenerator.initialValue();
                if (initialValue > 0) {
                    return " auto_increment = " + initialValue;
                }
            }
            HappyId happyId = field.getAnnotation(HappyId.class);
            if (happyId != null && happyId.initialVal() > 0) {
                return " auto_increment = " + happyId.initialVal();
            }
        }

        return "";
    }

    /**
     * 创建version字段字符串
     * @return version字段字符串
     * @throws Exception
     */
    private static String createVersionStringForAddTable() throws Exception {

        return "version int";
    }

    /**
     * (获取数据库字段类型)根据实体字段类型获取完整的数据库字段类型字符串, 包括数字的最大长度、数字的小数位数、字符串长度
     * @param entityField 实体字段
     * @return 完整的数据库字段类型字符串
     * @throws Exception
     */
    private static String getWholeDbFieldTypeByEntityFieldType(Field entityField) throws Exception {
        if (entityField.getType().isPrimitive()) {//基本类型
            return getDbFieldTypeByEntityFieldType(entityField);
        } else {
            Class fieldType = entityField.getType();
            Column column = entityField.getAnnotation(Column.class);
            HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
            boolean isText = false;
            int len = 0;
            int precision = 0;
            int scale = 0;
            if (column != null) {
                isText = !"".equals(column.columnDefinition());
                len = column.length();
                precision = column.precision();
                scale = column.scale();
            } else if (happyCol != null) {
                isText = happyCol.text() || happyCol.longText();
                len = happyCol.len();
                precision = happyCol.precision();
                scale = happyCol.scale();
            }
            if (fieldType == Date.class
                    || fieldType == java.util.Date.class
                    || fieldType == Timestamp.class
                    || fieldType == Byte.class
                    || fieldType == Short.class
                    || fieldType == Integer.class
                    || fieldType == Long.class
                    || fieldType == Float.class
                    || fieldType == Double.class
                    || fieldType == Boolean.class) {
                return getDbFieldTypeByEntityFieldType(entityField);
            } else if (fieldType == String.class) {
                if (isText) {
                    return getDbFieldTypeByEntityFieldType(entityField);
                }
                return getDbFieldTypeByEntityFieldType(entityField) + "(" + len + ")";
            } else if (fieldType == BigDecimal.class) {
                return getDbFieldTypeByEntityFieldType(entityField) + "(" + precision + "," + scale + ")";
            }
        }

        if (entityField.getType().isPrimitive()) {
            throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getGenericType().toString() + "), 没有找到合适的数据库字段类型");
        }
        throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getType() + "), 没有找到合适的数据库字段类型");
    }

    /**
     * (获取数据库字段类型)根据实体字段类型获取数据库字段类型字符串, 只有类型
     * @param entityField 实体字段
     * @return 数据库字段类型的字符串
     * @throws Exception
     */
    private static String getDbFieldTypeByEntityFieldType(Field entityField) throws Exception {
        if (entityField.getType().isPrimitive()) {//基本类型
            String typeStr = entityField.getGenericType().toString();
            if (typeStr.equals("byte")) {
                return "tinyint";
            } else if (typeStr.equals("short")) {
                return "smallint";
            } else if (typeStr.equals("int")) {
                return "int";
            } else if (typeStr.equals("long")) {
                return "bigint";
            } else if (typeStr.equals("float")) {
                return "float";
            } else if (typeStr.equals("double")) {
                return "double";
            } else if (typeStr.equals("boolean")) {
                return "bit";
            }
        } else {
            Class fieldType = entityField.getType();
            if (fieldType == String.class) {
                Column column = entityField.getAnnotation(Column.class);
                HappyCol happyCol = entityField.getAnnotation(HappyCol.class);
                if (column != null && !"".equals(column.columnDefinition())) {
                    return column.columnDefinition().trim();
                } else if (happyCol != null && happyCol.text()) {
                    return "text";
                } else if (happyCol != null && happyCol.longText()) {
                    return "longtext";
                } else if (happyCol != null && happyCol.fixLen()) {
                    return "char";
                }
                return "varchar";
            } else if (fieldType == Date.class || fieldType == java.util.Date.class || fieldType == Timestamp.class) {
                return "timestamp default current_timestamp";
            } else if (fieldType == BigDecimal.class) {
                return "decimal";
            } else if (fieldType == Byte.class) {
                return "tinyint";
            } else if (fieldType == Short.class) {
                return "smallint";
            } else if (fieldType == Integer.class) {
                return "int";
            } else if (fieldType == Long.class) {
                return "bigint";
            } else if (fieldType == Float.class) {
                return "float";
            } else if (fieldType == Double.class) {
                return "double";
            } else if (fieldType == Boolean.class) {
                return "bit";
            }
        }

        if (entityField.getType().isPrimitive()) {
            throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getGenericType().toString() + "), 没有找到合适的数据库字段类型");
        }
        throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getType() + "), 没有找到合适的数据库字段类型");
    }

    /**
     * 非空唯一
     * @param column column
     * @param happyCol happyCol
     * @return 非空唯一
     */
    private static boolean isNotNull(Column column, HappyCol happyCol) {
        boolean isNotNull = true;
        if (column != null) {
            isNotNull = !column.nullable();
        } else if (happyCol != null) {
            isNotNull = !happyCol.nullable();
        }

        return isNotNull;
    }

}
