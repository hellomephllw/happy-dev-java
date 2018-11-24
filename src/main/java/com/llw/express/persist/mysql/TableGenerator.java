package com.llw.express.persist.mysql;

import com.llw.util.FileUtil;
import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 表生成器
 * @author: llw
 * @date: 2018-11-22
 */
public class TableGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(TableGenerator.class);

    /**模式*/
    private static String _MODE;

    /**用户配置的基础包路径*/
    private static String _USER_CONFIG_BASE_PACKAGE_PATH;
    /**源码路径, 对根路径的补充*/
    private final static String _BASE_SOURCE_CODE_PATH = "/src/main/java/";

    /**检查模式*/
    private final static String _MODE_CHECK = "check";
    /**增量模式: 只执行增加的操作*/
    private final static String _MODE_INCREMENT = "increment";
    /**强制执行模式: 会执行删除和更新操作*/
    private final static String _MODE_FORCE = "force";

    /**
     * 生成表格
     * @param env 环境变量
     * @throws Exception
     */
    public static void generate(String env) throws Exception {
        //连接数据库
        DatabaseHelper.connectDatabase(env);
        //读取所有实体
        EntityReader.readAllEntities(getBasePackagePath());
        //对表格进行对比
        diff();
    }

    /**
     * 对表格进行对比: 可能进行更新
     * @throws Exception
     */
    private static void diff() throws Exception {
        List<Class> entities = EntityReader.getEntities();
        for (Class entity : entities) {
            boolean isEntity = false;
            String tableName = null;
            for (Annotation annotation : entity.getAnnotations()) {
                if (annotation.annotationType() == Entity.class) {
                    isEntity = true;
                }
                //获取表名
                if (annotation.annotationType() == Table.class) {
                    tableName = ((Table) annotation).name();
                }
            }
            //判断是否是实体
            if (isEntity) {
                if (tableName == null) {
                    logger.warn("实体缺少表名: " + entity);
                    continue;
                }
                //获取实体所有属性
                List<Field> fields = collectAllFields(entity);

                if (_MODE.equals(_MODE_CHECK)) {
                    //检查数据库表字段

                } else if (_MODE.equals(_MODE_INCREMENT)) {
                    //增量操作

                } else if (_MODE.equals(_MODE_FORCE)) {
                    //执行删改

                }
            }
        }
    }

    /**
     * 收集所有属性
     * @param entityClass 实体class
     * @return 所有属性
     * @throws Exception
     */
    private static List<Field> collectAllFields(Class entityClass) throws Exception {
        List<Field> fields = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            fields.add(field);
        }
        if (entityClass.getSuperclass() != Object.class) {
            fields.addAll(collectAllFields(entityClass.getSuperclass()));
        }

        return fields;
    }

    private static void diffCheck(String tableName, List<Field> fields) throws Exception {

    }

    /**
     * 获取用户提供的包的基本路径
     * @return 包路径
     * @throws Exception
     */
    public static String getBasePackagePath() throws Exception {
        if (getUserConfigBasePackageFilePath() == null) throw new Exception("用户没有配置包路径");

        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH + getUserConfigBasePackageFilePath();
    }

    /**
     * 获取用户配置包的文件路径
     * @return 用户配置包的文件路径
     * @throws Exception
     */
    public static String getUserConfigBasePackageFilePath() throws Exception {
        return _USER_CONFIG_BASE_PACKAGE_PATH;
    }

    /**
     * 获取用户项目构建的class路径
     * @return class路径
     * @throws Exception
     */
    public static String getBuildClassPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + "/build/classes/java/main";
    }

    /**
     * 初始化模式
     * @param mode 模式
     * @throws Exception
     */
    private static void initMode(String mode) throws Exception {
        _MODE = mode;
    }

    /**
     * 初始化用户配置的基础包路径
     * @param userConfigBasePackagePath 用户配置的基础包路径
     * @throws Exception
     */
    private static void initUserConfigBasePackagePath(String userConfigBasePackagePath) throws Exception {
        _USER_CONFIG_BASE_PACKAGE_PATH = userConfigBasePackagePath.replaceAll("\\.", "/");
    }

    public static void main(String[] args) {
        try {
            //初始化模式
            initMode(args[1]);
            //初始化用户配置的基础包路径
            initUserConfigBasePackagePath(args[2]);
            //生成表格
            generate(args[0]);
        } catch (Exception e) {
            LoggerUtil.printStackTrace(logger, e);
        } finally {
            try {
                Connection connection = DatabaseHelper.getConn();
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                LoggerUtil.printStackTrace(logger, e);
            }
        }
    }

}
