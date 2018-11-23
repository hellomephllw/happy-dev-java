package com.llw.express.persist.mysql;

import com.llw.express.code.BasicCodeGenerator;
import com.llw.util.FileUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Map;

/**
 * @description: 数据库
 * @author: llw
 * @date: 2018-11-23
 */
public class DatabaseHelper {

    /**项目资源目录路径的补充路径*/
    private final static String _PROJECT_RESOURCES_PATH = "/src/main/resources/";
    /**项目扩展lib目录路径的补充路径*/
    private final static String _PROJECT_LIB_PATH = "/extlib/";

    /**数据库url*/
    private static String databaseUrl;
    /**用户名*/
    private static String username;
    /**密码*/
    private static String password;
    /**数据库驱动包*/
    private static String databaseRiver;

    /**数据库连接*/
    private static Connection conn = null;

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

}
