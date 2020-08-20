package com.happy.express.persist.mysql;

import com.happy.express.persist.mysql.helper.DatabaseHappyHelper;
import com.happy.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.Statement;

/**
 * @description: 打成jar包后数据库表生成器
 * @author: liliwen
 * @date: 2020-08-20
 */
public class JarGenerator extends BaseGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(JarGenerator.class);

    /**
     * 生成数据库表
     * @param env 环境
     * @throws Exception
     */
    public static void generate(String env) throws Exception {
        //连接数据库
        DatabaseHappyHelper.connectDatabaseInJar(env);
        //读取所有实体
        EntityReader.readAllEntitiesInJarClassPath(getUserConfigBasePackageFilePath());
        //表对比并执行表同步任务
        HappyTableGenerator.diffAndGenerate();
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
                Statement statement = DatabaseHappyHelper.getStatement();
                Connection connection = DatabaseHappyHelper.getConn();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                LoggerUtil.printStackTrace(logger, e);
            }
        }
    }

}
