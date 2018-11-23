package com.llw.express.persist.mysql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 读取所有数据库表
 * @author: llw
 * @date: 2018-11-22
 */
public class TableReader {

    /**所有表格*/
    private static final List tables = new ArrayList<>();

    public static void readAllTables() throws Exception {
        Connection conn = DatabaseHelper.getConn();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet resultSet = metaData.getTables(null, "%", "%", new String[] {"TABLE"});
        while (resultSet.next()) {
            System.out.println("=====");
            System.out.println(resultSet);
        }
    }

}
