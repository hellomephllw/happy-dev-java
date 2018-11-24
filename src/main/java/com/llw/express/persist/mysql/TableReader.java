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

    public static void readAllTables() throws Exception {
        Connection conn = DatabaseHelper.getConn();
        DatabaseMetaData metaData = conn.getMetaData();
        ResultSet tableSet = metaData.getTables(DatabaseHelper.getDatabaseName(), "%", "%", new String[] {"TABLE"});
        while (tableSet.next()) {
            System.out.println("=====");
            System.out.println(tableSet.getString("TABLE_NAME"));
        }
        ResultSet columnSet = metaData.getColumns(DatabaseHelper.getDatabaseName(), "%", "%", "%");
        while (columnSet.next()) {
            System.out.println("#####");
            System.out.println(columnSet.getString("COLUMN_NAME"));
            System.out.println(columnSet.getString("TYPE_NAME"));
            System.out.println(columnSet.getString("COLUMN_SIZE"));
            System.out.println(columnSet.getString("DECIMAL_DIGITS"));
            System.out.println(columnSet.getString("NULLABLE"));
        }
        ResultSet indexSet = metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", "%", true, false);
        while (indexSet.next()) {
            System.out.println("~~~~~~");
            System.out.println(indexSet.getString("INDEX_NAME"));
            System.out.println(indexSet.getString("NON_UNIQUE"));
        }

    }

    public static void diff() throws Exception {

    }

}
