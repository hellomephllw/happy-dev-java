package com.llw.express.persist.mysql;

import com.llw.express.persist.mysql.helper.DatabaseHelper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * @description: 读取所有数据库表
 * @author: llw
 * @date: 2018-11-22
 */
public class TableReader {

    public static void readAllTables() throws Exception {
        Connection conn = DatabaseHelper.getConn();
        DatabaseMetaData metaData = conn.getMetaData();
//        ResultSet tableSet = metaData.getTables(DatabaseHelper.getDatabaseName(), "%", "demo_admdin", new String[] {"TABLE"});
//        System.out.println(tableSet.first());
//        while (tableSet.next()) {
//            System.out.println("=====");
//            System.out.println(tableSet.getString("TABLE_NAME"));
//        }
        ResultSet columnSet = metaData.getColumns(DatabaseHelper.getDatabaseName(), "%", "demo_admin", "aname");
        while (columnSet.next()) {
            System.out.println("#####");
            System.out.println(columnSet.getString("COLUMN_NAME"));
            System.out.println(columnSet.getString("TYPE_NAME"));
            System.out.println(columnSet.getInt("COLUMN_SIZE"));//精度，包括小数的总位数
            System.out.println(columnSet.getInt("DECIMAL_DIGITS"));//右侧小数位数
            System.out.println(columnSet.getInt("NULLABLE"));
        }
        System.out.println("!!!!!!");
        ResultSet indexSet = metaData.getIndexInfo(DatabaseHelper.getDatabaseName(), "%", "demo_wallet", true, true);
        while (indexSet.next()) {
            System.out.println("~~~~~~");
            System.out.println(indexSet.getString("INDEX_NAME"));
            System.out.println(indexSet.getBoolean("NON_UNIQUE"));
        }

    }

    public static void diff() throws Exception {

    }

}
