package com.happy.express.persist.mysql.helper;

import com.happy.express.persist.annotation.HappyCol;
import com.happy.express.persist.annotation.HappyId;
import com.happy.express.persist.annotation.HappyIndexes;
import com.happy.util.CollectionUtil;
import com.happy.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @description: 数据库处理工具
 * @author: llw
 * @date: 2020-08-19
 */
public class DatabaseHappyHelper extends BaseDatabaseHelper {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    //=======================操作数据库
    /**
     * 创建数据库表格
     * @param tableName 表名
     * @param entityFields 实体所有属性
     * @throws Exception
     */
    public static void addTable(String tableName, Class entity, List<Field> entityFields) throws Exception {
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
        logger.info("建表sql: " + sql.toString());
        statement.executeUpdate(sql.toString());

        logger.info("已创建数据库表: " + tableName);

        //创建唯一索引
        for (Field entityField : entityFields) {
            addUniqueIndex(tableName, entityField);
        }
        //添加索引
        addIndexes(entity, tableName);
    }

    /**
     * 删除表格
     * @param tableName 表名
     * @throws Exception
     */
    public static void deleteTable(String tableName) throws Exception {
        //删表sql
        String sql = "drop table " + tableName + ";";

        //执行删除
        logger.info("删除表格sql: " + sql);
        statement.executeUpdate(sql);

        logger.info("已删除数据库表: " + tableName);
    }

    /**
     * 添加数据库表字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void addField(String tableName, Field entityField) throws Exception {
        HappyCol column = entityField.getAnnotation(HappyCol.class);
        if (column == null) return ;

        //添加字段sql
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" add ");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(" ");
        sql.append(getWholeDbFieldTypeByEntityFieldType(entityField));
        if (!column.nullable() && !column.unique()) {
            sql.append(" not null");
        }
        sql.append(";");

        //执行添加字段
        logger.info("添加字段的sql: " + sql.toString());
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (!column.nullable() && !column.unique() ? " not null" : "");
        logger.info("为数据库表(" + tableName + ")添加字段: " + fieldStr);

        if (!column.nullable() && column.unique()) {
            logger.warn("【非常重要, 请注意】如果该字段为not null unique, 则忽略not null, 不然无法成功添加字段");
        }

        //创建唯一索引
        addUniqueIndex(tableName, entityField);
    }

    /**
     * 修改数据库表字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void modifyField(String tableName, Field entityField) throws Exception {
        HappyCol column = entityField.getAnnotation(HappyCol.class);
        HappyId id = entityField.getAnnotation(HappyId.class);
        if (id != null) {
            modifyId(tableName, entityField);
            return ;
        }
        if (column == null) return ;

        //修改sql字段
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" modify ");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(" ");
        sql.append(getWholeDbFieldTypeByEntityFieldType(entityField));
        if (!column.nullable() && !column.unique()) {
            sql.append(" not null");
        }
        sql.append(";");

        //执行修改字段
        logger.warn("修改字段的sql: " + sql.toString());
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (!column.nullable() && !column.unique() ? " not null" : "");
        logger.warn("把数据库表(" + tableName + ")字段(" + getDatabaseFieldName(entityField.getName()) + "), 修改为" + fieldStr);

        if (!column.nullable() && column.unique()) {
            logger.warn("【非常重要, 请注意】如果该字段为not null unique, 则忽略not null, 不然无法成功修改字段");
        }
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
        logger.warn("修改id字段的sql: " + sql);
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(version), 修改为id " + getWholeDbFieldTypeByEntityFieldType(entityField));
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
        logger.warn("删除字段的sql: " + sql);
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(" + dbFieldName + ")删除");
    }

    /**
     * 添加数据库字段唯一索引
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void addUniqueIndex(String tableName, Field entityField) throws Exception {
        HappyCol column = entityField.getAnnotation(HappyCol.class);
        if (column == null) return ;
        if (!column.unique()) return ;
        if (existUniqueIndex(tableName, entityField.getName())) return ;

        String uniqueIndexName = getUniqueIndexName(tableName, entityField.getName());
        StringBuilder sql = new StringBuilder();
        sql.append("create unique index");
        sql.append(" ");
        sql.append(uniqueIndexName);
        sql.append(" on ");
        sql.append(tableName);
        sql.append("(");
        sql.append(getDatabaseFieldName(entityField.getName()));
        sql.append(");");

        logger.info("添加唯一索引的sql: " + sql.toString());
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
        HappyCol column = entityField.getAnnotation(HappyCol.class);
        if (column == null) return ;
        if (column.unique()) return ;
        if (!existUniqueIndex(tableName, entityField.getName())) return ;

        String uniqueIndexName = getUniqueIndexName(tableName, entityField.getName());
        StringBuilder sql = new StringBuilder();
        sql.append("alter table");
        sql.append(" ");
        sql.append(tableName);
        sql.append(" drop index ");
        sql.append(getUniqueIndexName(tableName, entityField.getName()));
        sql.append(";");

        logger.info("删除唯一索引的sql: " + sql.toString());
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

        logger.info("删除唯一索引的sql: " + sql);
        statement.executeUpdate(sql);

        logger.info("把数据库表(" + tableName + ")的唯一索引(" + uniqueIndexName + ")删除");
    }

    /**
     * 添加索引
     * @param entity 实体
     * @param tableName 表名
     * @throws Exception
     */
    public static void addIndexes(Class entity, String tableName) throws Exception {
        HappyIndexes happyIndexes = (HappyIndexes) entity.getAnnotation(HappyIndexes.class);
        if (happyIndexes == null || happyIndexes.indexes() == null || happyIndexes.indexes().length == 0) return ;
        for (HappyIndexes.HappyIndex happyIndex : happyIndexes.indexes()) {
            String[] fields = happyIndex.fields();
            String suffix = happyIndex.suffix();
            addIndex(tableName, suffix, fields);
        }
    }

    /**
     * 添加索引
     * @param tableName 表名
     * @param suffix 后缀
     * @param fieldNames 字段名
     * @throws Exception
     */
    public static void addIndex(String tableName, String suffix, String... fieldNames) throws Exception {
        if (StringUtil.isEmpty(suffix)) {
            if (existIndex(tableName, getIndexNameFields(tableName, fieldNames))) return ;
        } else {
            if (existIndex(tableName, suffix)) return ;
        }

        String indexName = getIndexNameSuffix(tableName, suffix);
        if (StringUtil.isEmpty(suffix))
            indexName = getIndexNameFields(tableName, fieldNames);
        String sql = "create index " + indexName + " on " + tableName + "(" + getIndexCols(fieldNames) + ");";

        logger.info("添加索引的sql: " + sql);
        statement.executeUpdate(sql);

        logger.info("为数据库表(" + tableName + ")字段" + Arrays.asList(fieldNames).toString() + "添加索引(" + indexName + ")");
    }

    /**
     * 删除索引
     * @param tableName 表名
     * @param indexName 索引名
     * @throws Exception
     */
    public static void deleteIndex(String tableName, String indexName) throws Exception {
        String sql = "alter table " + tableName + " drop index " + indexName + ";";

        logger.info("删除索引的sql: " + sql);
        statement.executeUpdate(sql);

        logger.info("把数据库表(" + tableName + ")的索引(" + indexName + ")删除");
    }

    /**
     * 创建表字段字符串
     * @param entityField 实体字段
     * @return 表字段字符串
     * @throws Exception
     */
    private static String createFieldStringForAddTable(Field entityField) throws Exception {
        if (entityField.getAnnotation(HappyId.class) != null) {
            return createPrimaryKeyStringForAddTable();
        }
        if (entityField.getAnnotation(HappyCol.class) != null) {
            HappyCol column = entityField.getAnnotation(HappyCol.class);
            StringBuilder fieldLine = new StringBuilder();
            fieldLine.append(" ");
            fieldLine.append(getDatabaseFieldName(entityField.getName()));
            fieldLine.append(" ");
            fieldLine.append(getWholeDbFieldTypeByEntityFieldType(entityField));
            if (!column.nullable()) {
                fieldLine.append(" ");
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
            if (field.getAnnotation(HappyId.class) != null) {
                HappyId tableGenerator = field.getAnnotation(HappyId.class);
                int initialValue = tableGenerator.initialVal();
                if (initialValue > 0) {
                    return " auto_increment = " + initialValue;
                }
            }
        }

        return "";
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
                HappyCol column = entityField.getAnnotation(HappyCol.class);
                if (column.text() || column.longText()) return getDbFieldTypeByEntityFieldType(entityField);
                return getDbFieldTypeByEntityFieldType(entityField) + "(" + column.len() + ")";
            } else if (fieldType == BigDecimal.class) {
                HappyCol column = entityField.getAnnotation(HappyCol.class);
                return getDbFieldTypeByEntityFieldType(entityField) + "(" + column.precision() + "," + column.scale() + ")";
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
                HappyCol column = entityField.getAnnotation(HappyCol.class);
                if (column.fixLen() && column.len() > 0) return "char";
                if (column.text()) return "text";
                if (column.longText()) return "longtext";
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

}
