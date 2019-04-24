package com.happy.express.persist.mysql.helper;

import java.lang.reflect.Field;
import java.sql.ResultSet;

/**
 * @description: 通过实体处理数据库
 * @author: llw
 * @date: 2018-11-24
 */
public interface IFieldProcessor {

    public void byteField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void shortField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void integerField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void longField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void floatField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void doubleField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void booleanField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void stringField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void dateField(String tableName, Field field, ResultSet columnSet) throws Exception;

    public void bigDecimalField(String tableName, Field field, ResultSet columnSet) throws Exception;

}
