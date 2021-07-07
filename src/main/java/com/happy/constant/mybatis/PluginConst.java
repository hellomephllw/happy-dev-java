package com.happy.constant.mybatis;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.ReflectorFactory;

/**
 * @description:
 * @author: liliwen
 * @date: 2021-07-06
 */
public class PluginConst {

    public final static String DELEGATE_BOUNDSQL_SQL = "delegate.boundSql.sql";

    public final static String DELEGATE_BOUNDSQL = "delegate.boundSql";

    public final static String DELEGATE_MAPPEDSTATEMENT = "delegate.mappedStatement";

    public static final ReflectorFactory defaultReflectorFactory = new DefaultReflectorFactory();

}
