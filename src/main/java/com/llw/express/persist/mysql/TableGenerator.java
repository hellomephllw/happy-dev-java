package com.llw.express.persist.mysql;

import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * @description: 表生成器
 * @author: llw
 * @date: 2018-11-22
 */
public class TableGenerator {

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
        //读取所有表格
        TableReader.readAllTables();
    }

    public static void main(String[] args) {
        try {
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
