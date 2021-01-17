package com.happy.express.persist.mysql.helper;

import com.google.common.base.CaseFormat;
import com.happy.express.persist.mysql.ExtClassPathLoader;
import com.happy.util.FileUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Map;

/**
 * @description: 数据库工具基类
 * @author: llw
 * @date: 2020-08-19
 */
public abstract class BaseDatabaseHelper {

    /**项目资源目录路径的补充路径*/
    protected final static String _PROJECT_RESOURCES_PATH = "/src/main/resources/";
    /**项目扩展lib目录路径的补充路径*/
    protected final static String _PROJECT_LIB_PATH = "/extlib/";

    /**数据库url*/
    protected static String databaseUrl;
    /**数据库名称*/
    protected static String databaseName;
    /**用户名*/
    protected static String username;
    /**密码*/
    protected static String password;
    /**数据库驱动包*/
    protected static String databaseRiver;

    /**数据库连接*/
    protected static Connection conn = null;
    /**数据库元数据*/
    protected static DatabaseMetaData metaData = null;
    /**数据库声明*/
    protected static Statement statement = null;

    /**
     * 连接数据库
     * @param env 环境
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
     * 连接数据库
     * @param env 环境
     * @throws Exception
     */
    public static void connectDatabaseInJar(String env) throws Exception {
        //读取数据库配置
        readDatabaseConfigInJar(env);
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
        readConfigOut(datasourceConfig);
    }

    /**
     * 根据环境变量读取数据库配置
     * @param env 环境变量
     * @throws Exception
     */
    private static void readDatabaseConfigInJar(String env) throws Exception {
        if (env == null || "".equals(env)) throw new Exception("环境变了不能为空");

        String applicationFileName;
        if (env.equals("dev")) {
            applicationFileName = "application.yml";
        } else if (env.equals("test")) {
            applicationFileName = "application-test.yml";
        } else if (env.equals("prod")) {
            applicationFileName = "application-prod.yml";
        } else {
            throw new Exception("环境变量值只能是dev、test或prod");
        }

        //读取yaml
        Yaml yaml = new Yaml();
        InputStream inputStream = BaseDatabaseHelper.class.getResourceAsStream("/" + applicationFileName);
        Map<String, Map<String, Map<String, String>>> map = yaml.load(inputStream);
        Map<String, String> datasourceConfig = map.get("spring").get("datasource");
        //取出数据库配置
        readConfigOut(datasourceConfig);
    }

    /**
     * 取出数据库配置
     * @param datasourceConfig 配置
     * @throws Exception
     */
    private static void readConfigOut(Map<String, String> datasourceConfig) throws Exception {
        databaseUrl = datasourceConfig.get("url");
        username = datasourceConfig.get("username");
        password = datasourceConfig.get("password") + "";
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
     * 是否存在索引
     * @param tableName 表名
     * @param fieldNames 字段名
     * @return 存在索引
     * @throws Exception
     */
    public static boolean existIndex(String tableName, String... fieldNames) throws Exception {
        ResultSet indexSet = metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", tableName, false, true);
        while (indexSet.next()) {
            String indexName = indexSet.getString("INDEX_NAME");
            if (indexName.equals(getIndexName(tableName, fieldNames))) {
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
     * 根据表名获取该表中所有索引
     * @param tableName 数据库表
     * @return 所有索引
     * @throws Exception
     */
    public static ResultSet getAllIndexByTableName(String tableName) throws Exception {

        return metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", tableName, false, true);
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
     * 获取索引名称
     * @param tableName 表明
     * @param fieldNames 字段名
     * @return 索引名称
     * @throws Exception
     */
    public static String getIndexName(String tableName, String... fieldNames) throws Exception {
        String cols = "";
        int i = 0;
        for (String fieldName : fieldNames) {
            if (i++ != 0) cols += "_";
            cols += fieldName;
        }

        return tableName + "__index__" + cols;
    }

    /**
     * 获取索引列
     * @param fieldNames 字段名
     * @return 索引列
     * @throws Exception
     */
    public static String getIndexCols(String... fieldNames) throws Exception {
        String cols = "";
        int i = 0;
        for (String fieldName : fieldNames) {
            if (i++ != 0) cols += ",";
            cols += getDatabaseFieldName(fieldName);
        }

        return cols;
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

}
