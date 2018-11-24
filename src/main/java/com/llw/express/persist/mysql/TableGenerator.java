package com.llw.express.persist.mysql;

import com.llw.express.persist.mysql.helper.*;
import com.llw.util.FileUtil;
import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
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
        TableReader.readAllTables();
    }

    /**
     * 对表格进行对比: 可能进行更新
     * @throws Exception
     */
    private static void diff() throws Exception {
        List<Class> entities = EntityReader.getEntities();
        /**通过从实体正向检查和修改数据库表格*/
        for (Class entity : entities) {
            boolean isEntity = false;
            String tableName = null;
            for (Annotation annotation : entity.getAnnotations()) {
                if (annotation.annotationType() == Entity.class) {
                    isEntity = true;
                }
                //获取表名
                if (annotation.annotationType() == Table.class) {
                    tableName = ((Table) annotation).name().toLowerCase();
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

                //属性字段安全检查
                //todo

                if (_MODE.equals(_MODE_CHECK)) {
                    //检查数据库表字段
                    if (!DatabaseHelper.existTable(tableName)) {
                        logger.warn("数据库中不存在表: " + tableName);
                        continue;
                    }
                    //通过从实体属性到数据库字段正向对比
                    for (Field entityField : fields) {
                        //过滤实体非column字段
                        if (!isColumn(entityField)) continue;
                        //判断是否存在
                        String entityFieldName = entityField.getName();
                        if (!DatabaseHelper.existField(tableName, entityFieldName)) {
                            logger.warn("数据库表(" + tableName + ")中不存在字段: " + entityFieldName);
                            continue;
                        }
                        //对比
                        fieldsProcessor(tableName, entityField, new FieldChecker());
                    }
                    //通过从数据库字段到实体属性反向对比(看数据库表格是否有多余的字段和唯一索引)
                    //todo
                } else if (_MODE.equals(_MODE_INCREMENT)) {
                    //增量操作
                    //todo
                } else if (_MODE.equals(_MODE_FORCE)) {
                    //执行删改
                    //todo
                }
            }
        }
        /**通过从数据库表格逆向检查和修改数据库表格(看是否有多余的表格)*/
        //todo
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

    /**
     * 属性处理器
     * @param tableName 表名
     * @param entityField 实体属性
     * @param fieldHelper 处理器
     * @throws Exception
     */
    private static void fieldsProcessor(String tableName, Field entityField, IFieldHelper fieldHelper) throws Exception {
        ResultSet columnSet = DatabaseHelper.getField(tableName, entityField.getName());
        if (entityField.getType().isPrimitive()) {//基本类型
            String typeStr = entityField.getGenericType().toString();
            if (typeStr.equals("int")) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).integerField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (typeStr.equals("long")) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).longField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (typeStr.equals("float")) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).floatField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (typeStr.equals("double")) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).doubleField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (typeStr.equals("boolean")) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).booleanField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            }
        } else {
            Class fieldType = entityField.getType();
            if (fieldType == String.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).stringField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Date.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).dateField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == BigDecimal.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).bigDecimalField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Integer.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).integerField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Long.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).longField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Float.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).floatField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Double.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).doubleField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            } else if (fieldType == Boolean.class) {
                if (fieldHelper instanceof IFieldProcessor) {
                    ((IFieldProcessor) fieldHelper).booleanField(tableName, entityField, columnSet);
                } else {
//                    ((IFieldReverseProcessor) fieldHelper).integerField(entityField, columnSet);
                }
            }
        }
    }

    /**
     * 是数据库字段
     * @param field 实体字段
     * @return 是否是数据库字段
     * @throws Exception
     */
    private static boolean isColumn(Field field) throws Exception {
        return field.getAnnotation(Column.class) != null
                || field.getAnnotation(Id.class) != null
                || field.getAnnotation(Version.class) != null;
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
