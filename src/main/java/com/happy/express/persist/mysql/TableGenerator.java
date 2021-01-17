package com.happy.express.persist.mysql;

import com.happy.express.persist.mysql.helper.*;
import com.happy.util.FileUtil;
import com.happy.util.LoggerUtil;
import com.happy.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

/**
 * @description: 表生成器
 * @author: llw
 * @date: 2018-11-22
 */
public class TableGenerator extends BaseGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(TableGenerator.class);

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

        /**实体安全检查*/
        if (!entitiesIsSecurity(entities)) return ;

        /**通过从实体正向检查和修改数据库表格*/
        for (Class entity : entities) {
            boolean isEntity = ReflectionUtil.isEntity(entity);
            String tableName = ReflectionUtil.getTableName(entity);
            //判断是否是实体
            if (isEntity) {
                //表名检查
                if (tableName == null) {
                    logger.error("实体缺少表名: " + entity);
                    continue;
                }
                //获取实体所有属性
                List<Field> fields = collectAllFields(entity);

                //属性字段安全检查
                if (!fieldsIsSecurity(entity, fields)) continue;

                if (_MODE.equals(_MODE_CHECK)) {
                    /**检查*/
                    //检查数据库表是否存在
                    if (!DatabaseHelper.existTable(tableName)) {
                        logger.info("数据库中不存在表: " + tableName);
                        continue;
                    }
                    //通过从实体属性到数据库字段正向对比
                    for (Field entityField : fields) {
                        //过滤实体非column字段
                        if (!isColumn(entityField)) continue;
                        //判断是否存在
                        String entityFieldName = entityField.getName();
                        if (!DatabaseHelper.existField(tableName, entityFieldName)) {
                            logger.info("数据库表(" + tableName + ")中不存在字段: " + entityFieldName);
                            continue;
                        }
                        //对比
                        fieldsProcessor(tableName, entityField, new FieldChecker());
                    }
                    //检查索引
                    checkIndexes(entity, tableName);
                    //通过从数据库字段到实体属性反向对比(看数据库表格是否有多余的字段和唯一索引)
                    dbTableUnusedChecker(tableName, entity, fields, new FieldReverseChecker(), false);
                } else if (_MODE.equals(_MODE_INCREMENT)) {
                    /**增量操作*/
                    //创建数据库表
                    if (!DatabaseHelper.existTable(tableName)) {
                        DatabaseHelper.addTable(tableName, fields);
                        continue;
                    }

                    //创建数据库字段和唯一索引
                    for (Field entityField : fields) {
                        //过滤实体非column字段
                        if (!isColumn(entityField)) continue;

                        //如果不存在, 则新增字段
                        String entityFieldName = entityField.getName();
                        if (!DatabaseHelper.existField(tableName, entityFieldName)) {
                            DatabaseHelper.addField(tableName, entityField);
                        }
                    }
                    //添加索引(只有happyIndex提供建立索引功能)
                    DatabaseHappyHelper.addIndexes(entity, tableName);
                } else if (_MODE.equals(_MODE_FORCE)) {
                    /**执行增删改*/
                    //创建数据库表
                    if (!DatabaseHelper.existTable(tableName)) {
                        DatabaseHelper.addTable(tableName, fields);
                        continue;
                    }

                    //通过从实体属性到数据库字段正向对比
                    for (Field entityField : fields) {
                        //过滤实体非column字段
                        if (!isColumn(entityField)) continue;
                        //新增字段
                        String entityFieldName = entityField.getName();
                        if (!DatabaseHelper.existField(tableName, entityFieldName)) {
                            DatabaseHelper.addField(tableName, entityField);
                            continue;
                        }
                        //修改字段
                        fieldsProcessor(tableName, entityField, new FieldForcer());
                    }
                    //添加索引(只有happyIndex提供建立索引功能)
                    DatabaseHappyHelper.addIndexes(entity, tableName);
                    //通过从数据库字段到实体属性反向对比(看数据库表格是否有多余的字段和唯一索引)
                    dbTableUnusedChecker(tableName, entity, fields, new FieldReverseForcer(), false);
                }
            }
        }
        /**通过从数据库表格逆向检查和修改数据库表格(看是否有多余的表格)*/
        dbUnusedChecker(entities);

        logger.info(">>>>>>>动态检查(创建/修改)表格任务执行完毕, 本次操作为: " + _MODE);
    }

    /**
     * 判断实体是否安全
     * @param entities 所有实体
     * @return 是否安全
     * @throws Exception
     */
    private static boolean entitiesIsSecurity(List<Class> entities) throws Exception {
        boolean isSecurity = true;
        for (Class entityClass : entities) {
            boolean isEntity = ReflectionUtil.isEntity(entityClass);
            String tableName = ReflectionUtil.getTableName(entityClass);

            if (isEntity) {
                //检查缺少表名的实体
                if (tableName == null) {
                    logger.error("实体缺少表名: " + entityClass);
                    isSecurity = false;
                    continue;
                }
                //获取实体所有属性
                List<Field> fields = collectAllFields(entityClass);

                //属性字段安全检查
                if (!fieldsIsSecurity(entityClass, fields)) {
                    isSecurity = false;
                }
            }
        }

        return isSecurity;
    }

    /**
     * 持久化对象的字段是否安全
     * @param entityClass 实体class
     * @param fields 所有字段
     * @return 是否安全
     * @throws Exception
     */
    private static boolean fieldsIsSecurity(Class entityClass, List<Field> fields) throws Exception {
        boolean hasIdAnnotation = false;
        boolean hasId = false;
        int idAnnotationAmount = 0;
        for (Field field : fields) {
            //是否有id
            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                hasIdAnnotation = true;
                ++idAnnotationAmount;
            }
            if ("id".equals(field.getName())) {
                hasId = true;
            }

            //检查string是否设置长度
            if (field.getType() == String.class) {
                Column column = field.getAnnotation(Column.class);
                if (column != null && column.length() == 255 && "".equals(column.columnDefinition())) {
                    logger.error("实体(" + entityClass.getSimpleName() + ")的字段(" + field.getName() + ")为string类型, 需要设置字符串长度");
                    return false;
                }
            }

            //检查bigDecimal是否设置最大长度和小数位数
            if (field.getType() == BigDecimal.class) {
                Column column = field.getAnnotation(Column.class);
                if (column != null) {
                    boolean pass = true;
                    if (column.precision() == 0) {
                        logger.error("实体(" + entityClass.getSimpleName() + ")的字段(" + field.getName() + ")为bigDecimal类型, 需要设置最大长度");
                        pass = false;
                    }
                    if (column.scale() == 0) {
                        logger.error("实体(" + entityClass.getSimpleName() + ")的字段(" + field.getName() + ")为bigDecimal类型, 需要设置小数位数");
                        pass = false;
                    }
                    if (!pass) {
                        return false;
                    }
                }
            }
        }
        //是否有id
        if (!hasId) {
            logger.error("实体(" + entityClass.getSimpleName() + ")没有id字段");
        }
        if (!hasIdAnnotation) {
            logger.error("实体(" + entityClass.getSimpleName() + ")没有id注解");
        }
        //id注解个数大于1
        if (idAnnotationAmount > 1) {
            logger.error("实体(" + entityClass.getSimpleName() + ")的id注解只能有1个");
        }

        if (!hasIdAnnotation || !hasId || idAnnotationAmount > 1) {
            return false;
        }

        return true;
    }

    /**
     * 检查数据库中多余的表格
     * @param entities 所有实体
     * @throws Exception
     */
    private static void dbUnusedChecker(List<Class> entities) throws Exception {
        ResultSet tableSet = DatabaseHelper.getAllTables();
        while (tableSet.next()) {
            String tableName = tableSet.getString("TABLE_NAME");
            boolean exist = false;
            for (Class entityClass : entities) {
                Entity entityAnnotation = (Entity) entityClass.getAnnotation(Entity.class);
                Table tableAnnotation = (Table) entityClass.getAnnotation(Table.class);
                if (entityAnnotation != null
                        && tableAnnotation != null
                        && tableAnnotation.name().toLowerCase().equals(tableName)) {
                    exist = true;
                    break;
                }
            }
            if (!exist) {
                if (_MODE.equals(_MODE_CHECK)) {
                    logger.warn("数据库表(" + tableName + ")在实体中不存在，需要删除");
                }
                if (_MODE.equals(_MODE_FORCE)) {
                    DatabaseHelper.deleteTable(tableName);
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
     * 获取用户项目构建的class路径
     * @return class路径
     * @throws Exception
     */
    public static String getBuildClassPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + "/build/classes/java/main";
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
                Statement statement = DatabaseHelper.getStatement();
                Connection connection = DatabaseHelper.getConn();
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                LoggerUtil.printStackTrace(logger, e);
            }
        }
    }

}
