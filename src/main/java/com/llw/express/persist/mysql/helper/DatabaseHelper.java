package com.llw.express.persist.mysql.helper;

import com.google.common.base.CaseFormat;
import com.llw.express.persist.mysql.ExtClassPathLoader;
import com.llw.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Version;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;

/**
 * @description: 数据库
 * @author: llw
 * @date: 2018-11-23
 */
public class DatabaseHelper {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    /**项目资源目录路径的补充路径*/
    private final static String _PROJECT_RESOURCES_PATH = "/src/main/resources/";
    /**项目扩展lib目录路径的补充路径*/
    private final static String _PROJECT_LIB_PATH = "/extlib/";

    /**数据库url*/
    private static String databaseUrl;
    /**数据库名称*/
    private static String databaseName;
    /**用户名*/
    private static String username;
    /**密码*/
    private static String password;
    /**数据库驱动包*/
    private static String databaseRiver;

    /**数据库连接*/
    private static Connection conn = null;
    /**数据库元数据*/
    private static DatabaseMetaData metaData = null;
    /**数据库声明*/
    private static Statement statement = null;

    /**
     * 连接数据库
     * @throws Exception
     */
    public static void connectDatabase(String env) throws Exception {
        //读取数据库配置
        readDatabaseConfig(env);
        //加载jdbc相关jar包到classpath
        ExtClassPathLoader.loadAllJars(getProjectLibPath());
        //加载驱动类
        Class.forName(databaseRiver);
        //获取数据库连接
        conn = DriverManager.getConnection(databaseUrl, username, password);
        //初始化数据库元数据
        metaData = conn.getMetaData();
        //初始化数据库声明
        statement = conn.createStatement();
    }

    /**
     * 根据环境变量读取数据库配置
     * @param env 环境变量
     * @throws Exception
     */
    private static void readDatabaseConfig(String env) throws Exception {
        if (env == null || "".equals(env)) throw new Exception("环境变了不能为空");

        String ymlFilePath = getProjectResourcesPath();
        if (env.equals("dev")) {
            ymlFilePath += "application.yml";
        } else if (env.equals("test")) {
            ymlFilePath += "application-test.yml";
        } else if (env.equals("prod")) {
            ymlFilePath += "application-prod.yml";
        } else {
            throw new Exception("环境变量值只能是dev、test或prod");
        }

        //读取yaml
        File ymlFile = new File(ymlFilePath);
        Yaml yaml = new Yaml();
        Map<String, Map<String, Map<String, String>>> map = yaml.load(new FileInputStream(ymlFile));
        Map<String, String> datasourceConfig = map.get("spring").get("datasource");
        //取出数据库配置
        databaseUrl = datasourceConfig.get("url");
        username = datasourceConfig.get("username");
        password = datasourceConfig.get("password");
        databaseRiver = datasourceConfig.get("driver-class-name");
        String[] fragments = databaseUrl.split("\\?")[0].split("/");
        databaseName = fragments[fragments.length - 1];
    }

    /**
     * 获取项目lib资源路径
     * @return 项目lib资源路径
     * @throws Exception
     */
    public static String getProjectLibPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + _PROJECT_LIB_PATH;
    }

    /**
     * 获取项目资源目录路径
     * @return 项目资源目录路径
     * @throws Exception
     */
    public static String getProjectResourcesPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + _PROJECT_RESOURCES_PATH;
    }

    /**
     * 获取数据库连接
     * @return 数据库连接
     * @throws Exception
     */
    public static Connection getConn() throws Exception {
        return conn;
    }

    /**
     * 获取数据库声明
     * @return 数据库声明
     * @throws Exception
     */
    public static Statement getStatement() throws Exception {
        return statement;
    }

    /**
     * 获取数据库名称
     * @return 数据库名称
     */
    public static String getDatabaseName() {
        return databaseName;
    }

    //=======================获取数据库表信息
    /**
     * 数据库中存在该表
     * @param tableName 表名
     * @return 是否存在
     * @throws Exception
     */
    public static boolean existTable(String tableName) throws Exception {

        return metaData.getTables(getDatabaseName(), "%", tableName, new String[] {"TABLE"}).next();
    }

    /**
     * 数据库表中存在该字段
     * @param tableName 表名
     * @param entityFieldName 实体字段名
     * @return 是否存在
     * @throws Exception
     */
    public static boolean existField(String tableName, String entityFieldName) throws Exception {

        return metaData.getColumns(DatabaseHelper.getDatabaseName(), "%", tableName, getDatabaseFieldName(entityFieldName)).next();
    }

    /**
     * 数据库表中存在该唯一索引
     * @param tableName 表名
     * @param entityFieldName 实体字段名
     * @return 是否存在
     * @throws Exception
     */
    public static boolean existUniqueIndex(String tableName, String entityFieldName) throws Exception {
        ResultSet uniqueSet = metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", tableName, true, true);
        while (uniqueSet.next()) {
            String uniqueName = uniqueSet.getString("INDEX_NAME");
            if (uniqueName.equals(getUniqueIndexName(tableName, entityFieldName))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 获取所有表格
     * @return 所有表格
     * @throws Exception
     */
    public static ResultSet getAllTables() throws Exception {

        return metaData.getTables(DatabaseHelper.getDatabaseName(), "%", "%", new String[] {"TABLE"});
    }

    /**
     * 获取数据库表的所有字段
     * @param tableName 数据库表
     * @return 所有字段
     * @throws Exception
     */
    public static ResultSet getAllFieldsByTableName(String tableName) throws Exception {

        return metaData.getColumns(DatabaseHelper.getDatabaseName(), "%", tableName, "%");
    }

    /**
     * 获取数据库表字段
     * @param tableName 表名
     * @param entityFieldName 实体字段名
     * @return 字段
     * @throws Exception
     */
    public static ResultSet getField(String tableName, String entityFieldName) throws Exception {

        ResultSet columnSet = metaData.getColumns(DatabaseHelper.getDatabaseName(), "%", tableName, getDatabaseFieldName(entityFieldName));

        return columnSet.next() ? columnSet : null;
    }

    /**
     * 根据表名获取该表中所有唯一索引
     * @param tableName 数据库表
     * @return 所有唯一索引
     * @throws Exception
     */
    public static ResultSet getAllUniqueIndexByTableName(String tableName) throws Exception {

        return metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", tableName, true, true);
    }

    /**
     * 获取唯一索引名称
     * @param tableName 表名
     * @param entityFieldName 实体字段名称
     * @return 该实体字段的索引名称
     * @throws Exception
     */
    public static String getUniqueIndexName(String tableName, String entityFieldName) throws Exception {

        return tableName + "_unique_" + getDatabaseFieldName(entityFieldName);
    }

    /**
     * 根据实体字段名(驼峰)获取数据库字段名(下划线)
     * @param entityFieldName 实体字段名
     * @return 数据库字段名
     * @throws Exception
     */
    public static String getDatabaseFieldName(String entityFieldName) throws Exception {

        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entityFieldName);
    }

    /**
     * 根据数据库字段名(下划线)获取实体字段名(驼峰)
     * @param databaseFieldName 数据库字段名
     * @return 实体字段名
     * @throws Exception
     */
    public static String getEntityFieldName(String databaseFieldName) throws Exception {

        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, databaseFieldName);
    }

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
        sql.append(");");

        //执行建表
        statement.executeUpdate(sql.toString());

        logger.info("已创建数据库表: " + tableName);
        logger.info("建表sql: " + sql.toString());

        //创建独立索引
        for (Field entityField : entityFields) {
            addUniqueIndex(tableName, entityField);
        }
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
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (!column.nullable() && !column.unique() ? " not null" : "");
        logger.info("为数据库表(" + tableName + ")添加字段: " + fieldStr);
        logger.info("添加字段的sql: " + sql.toString());

        if (!column.nullable() && column.unique()) {
            logger.warn("【非常重要, 请注意】如果该字段为not null unique, 则忽略not null, 不然无法成功添加字段");
        }

        //创建独立索引
        addUniqueIndex(tableName, entityField);
    }

    /**
     * 修改数据库表字段
     * @param tableName 表名
     * @param entityField 实体字段
     * @throws Exception
     */
    public static void modifyField(String tableName, Field entityField) throws Exception {
        Column column = entityField.getAnnotation(Column.class);
        Id id = entityField.getAnnotation(Id.class);
        Version version = entityField.getAnnotation(Version.class);
        if (id != null) {
            modifyId(tableName, entityField);
            return ;
        }
        if (version != null) {
            modifyVersion(tableName, entityField);
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
        } else {
            sql.append(" null");
        }
        sql.append(";");

        //执行修改字段
        statement.executeUpdate(sql.toString());

        String fieldStr = getDatabaseFieldName(entityField.getName())
                + " "
                + getWholeDbFieldTypeByEntityFieldType(entityField)
                + (!column.nullable() && !column.unique() ? " not null" : " null");
        logger.warn("把数据库表(" + tableName + ")字段(" + getDatabaseFieldName(entityField.getName()) + "), 修改为" + fieldStr);
        logger.warn("修改字段的sql: " + sql.toString());

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
        statement.executeUpdate(sql);

        logger.warn("把数据库表(" + tableName + ")字段(version), 修改为id " + getWholeDbFieldTypeByEntityFieldType(entityField));
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
        if (entityField.getAnnotation(Id.class) != null) {
            return createPrimaryKeyStringForAddTable();
        }
        if (entityField.getAnnotation(Version.class) != null) {
            return createVersionStringForAddTable();
        }
        if (entityField.getAnnotation(Column.class) != null) {
            Column column = entityField.getAnnotation(Column.class);
            StringBuilder fieldLine = new StringBuilder();
            fieldLine.append(" ");
            fieldLine.append(getDatabaseFieldName(entityField.getName()));
            fieldLine.append(" ");
            fieldLine.append(getWholeDbFieldTypeByEntityFieldType(entityField));
            fieldLine.append(" ");
            if (!column.nullable()) {
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

        return "id bigint auto_increment primary key not null";
    }

    /**
     * 创建version字段字符串
     * @return version字段字符串
     * @throws Exception
     */
    private static String createVersionStringForAddTable() throws Exception {

        return "version bigint";
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
                    || fieldType == Integer.class
                    || fieldType == Long.class
                    || fieldType == Float.class
                    || fieldType == Double.class
                    || fieldType == Boolean.class) {
                return getDbFieldTypeByEntityFieldType(entityField);
            } else if (fieldType == String.class) {
                Column column = entityField.getAnnotation(Column.class);
                if (!"".equals(column.columnDefinition())) {
                    return getDbFieldTypeByEntityFieldType(entityField);
                }
                return getDbFieldTypeByEntityFieldType(entityField) + "(" + column.length() + ")";
            } else if (fieldType == BigDecimal.class) {
                Column column = entityField.getAnnotation(Column.class);
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
            if (typeStr.equals("int")) {
                return "int";
            } else if (typeStr.equals("long")) {
                return "bigint";
            } else if (typeStr.equals("float")) {
                return "float";
            } else if (typeStr.equals("double")) {
                return "double";
            } else if (typeStr.equals("boolean")) {
                return "int";
            }
        } else {
            Class fieldType = entityField.getType();
            if (fieldType == String.class) {
                Column column = entityField.getAnnotation(Column.class);
                if (!"".equals(column.columnDefinition())) {
                    return column.columnDefinition().trim();
                }
                return "varchar";
            } else if (fieldType == Date.class) {
                return "timestamp";
            } else if (fieldType == BigDecimal.class) {
                return "decimal";
            } else if (fieldType == Integer.class) {
                return "int";
            } else if (fieldType == Long.class) {
                return "bigint";
            } else if (fieldType == Float.class) {
                return "float";
            } else if (fieldType == Double.class) {
                return "double";
            } else if (fieldType == Boolean.class) {
                return "int";
            }
        }

        if (entityField.getType().isPrimitive()) {
            throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getGenericType().toString() + "), 没有找到合适的数据库字段类型");
        }
        throw new Exception("字段(" + getDatabaseFieldName(entityField.getName()) + ")的类型为(" + entityField.getType() + "), 没有找到合适的数据库字段类型");
    }

}
