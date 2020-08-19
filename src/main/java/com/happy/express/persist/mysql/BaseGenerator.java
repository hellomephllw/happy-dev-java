package com.happy.express.persist.mysql;

import com.happy.express.persist.annotation.HappyCol;
import com.happy.express.persist.mysql.helper.DatabaseHelper;
import com.happy.express.persist.mysql.helper.IFieldProcessor;
import com.happy.express.persist.mysql.helper.IFieldReverseProcessor;
import com.happy.util.FileUtil;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 表生成器基类
 * @author: llw
 * @date: 2020-08-18
 */
public class BaseGenerator {

    /**模式*/
    protected static String _MODE;

    /**用户配置的基础包路径*/
    protected static String _USER_CONFIG_BASE_PACKAGE_PATH;
    /**源码路径, 对根路径的补充*/
    protected final static String _BASE_SOURCE_CODE_PATH = "/src/main/java/";

    /**检查模式*/
    protected final static String _MODE_CHECK = "check";
    /**增量模式: 只执行增加的操作*/
    protected final static String _MODE_INCREMENT = "increment";
    /**强制执行模式: 会执行删除和更新操作*/
    protected final static String _MODE_FORCE = "force";


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
     * 收集所有属性
     * @param entityClass 实体class
     * @return 所有属性
     * @throws Exception
     */
    public static List<Field> collectAllFields(Class entityClass) throws Exception {
        List<Field> fields = new ArrayList<>();

        for (Field field : entityClass.getDeclaredFields()) {
            fields.add(field);
        }
        if (entityClass.getSuperclass() != Object.class) {
            fields.addAll(collectAllFields(entityClass.getSuperclass()));
        }

        //id first
        for (Field field : fields) {
            if ("id".equals(field.getName())) {
                fields.remove(field);
                fields.add(0, field);
                break;
            }
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
    protected static void fieldsProcessor(String tableName, Field entityField, IFieldProcessor fieldHelper) throws Exception {
        ResultSet columnSet = DatabaseHelper.getField(tableName, entityField.getName());
        if (columnSet == null) return ;
        if (entityField.getType().isPrimitive()) {//基本类型
            String typeStr = entityField.getGenericType().toString();
            if (typeStr.equals("byte")) {
                fieldHelper.byteField(tableName, entityField, columnSet);
            } else if (typeStr.equals("short")) {
                fieldHelper.shortField(tableName, entityField, columnSet);
            } else if (typeStr.equals("int")) {
                fieldHelper.integerField(tableName, entityField, columnSet);
            } else if (typeStr.equals("long")) {
                fieldHelper.longField(tableName, entityField, columnSet);
            } else if (typeStr.equals("float")) {
                fieldHelper.floatField(tableName, entityField, columnSet);
            } else if (typeStr.equals("double")) {
                fieldHelper.doubleField(tableName, entityField, columnSet);
            } else if (typeStr.equals("boolean")) {
                fieldHelper.booleanField(tableName, entityField, columnSet);
            }
        } else {
            Class fieldType = entityField.getType();
            if (fieldType == String.class) {
                fieldHelper.stringField(tableName, entityField, columnSet);
            } else if (fieldType == Date.class || fieldType == java.util.Date.class || fieldType == Timestamp.class) {
                fieldHelper.dateField(tableName, entityField, columnSet);
            } else if (fieldType == BigDecimal.class) {
                fieldHelper.bigDecimalField(tableName, entityField, columnSet);
            } else if (fieldType == Byte.class) {
                fieldHelper.byteField(tableName, entityField, columnSet);
            } else if (fieldType == Short.class) {
                fieldHelper.shortField(tableName, entityField, columnSet);
            } else if (fieldType == Integer.class) {
                fieldHelper.integerField(tableName, entityField, columnSet);
            } else if (fieldType == Long.class) {
                fieldHelper.longField(tableName, entityField, columnSet);
            } else if (fieldType == Float.class) {
                fieldHelper.floatField(tableName, entityField, columnSet);
            } else if (fieldType == Double.class) {
                fieldHelper.doubleField(tableName, entityField, columnSet);
            } else if (fieldType == Boolean.class) {
                fieldHelper.booleanField(tableName, entityField, columnSet);
            }
        }
    }

    /**
     * 检查数据库表格多余的字段和唯一索引
     * @param tableName 表名
     * @param fields 字段集合
     * @param isHappyDev 是happyDev的注解
     * @throws Exception
     */
    protected static void dbTableUnusedChecker(String tableName, List<Field> fields, IFieldReverseProcessor fieldReverseProcessor, boolean isHappyDev) throws Exception {
        //检查多余的字段
        ResultSet columnSet = DatabaseHelper.getAllFieldsByTableName(tableName);
        while (columnSet.next()) {
            String dbFieldName = columnSet.getString("COLUMN_NAME");
            boolean existInEntity = false;
            for (Field field : fields) {
                if (field.getName().equals(DatabaseHelper.getEntityFieldName(dbFieldName))) {
                    existInEntity = true;
                    break;
                }
            }
            if (!existInEntity) {
                fieldReverseProcessor.unusedField(tableName, dbFieldName);
            }
        }
        //检查多余的唯一索引
        ResultSet uniqueSet = DatabaseHelper.getAllUniqueIndexByTableName(tableName);
        while (uniqueSet.next()) {
            String uniqueIndexName = uniqueSet.getString("INDEX_NAME");
            if (uniqueIndexName.toLowerCase().equals("primary")) {
                continue;
            }
            String dbFieldName = uniqueIndexName.split("_unique_")[1];
            boolean existInField = false;
            for (Field field : fields) {
                if (field.getName().equals(DatabaseHelper.getEntityFieldName(dbFieldName))) {
                    if (isHappyDev) {
                        HappyCol column = field.getAnnotation(HappyCol.class);
                        if (column.unique()) {
                            //属性中配置了唯一索引
                            existInField = true;
                        }
                        break;
                    } else {
                        Column column = field.getAnnotation(Column.class);
                        if (column.unique()) {
                            //属性中配置了唯一索引
                            existInField = true;
                        }
                        break;
                    }
                }
            }
            if (!existInField) {
                fieldReverseProcessor.unusedUniqueIndex(tableName, uniqueIndexName);
            }
        }
    }

    /**
     * 初始化模式
     * @param mode 模式
     * @throws Exception
     */
    protected static void initMode(String mode) throws Exception {
        _MODE = mode;
    }

    /**
     * 初始化用户配置的基础包路径
     * @param userConfigBasePackagePath 用户配置的基础包路径
     * @throws Exception
     */
    protected static void initUserConfigBasePackagePath(String userConfigBasePackagePath) throws Exception {
        _USER_CONFIG_BASE_PACKAGE_PATH = userConfigBasePackagePath.replaceAll("\\.", "/");
    }

}
