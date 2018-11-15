package com.llw.util;

import java.util.Date;

/**
 * @discription: 日期工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class DateUtil {

    /**
     * 把util date转换为sql date
     * @param date util date
     * @return sql date
     * @throws Exception
     */
    public static java.sql.Date transformUtilDateToSqlDate(Date date) throws Exception {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 把sql date转换为util date
     * @param date sql date
     * @return util date
     * @throws Exception
     */
    public static Date transformSqlDateToUtilDate(java.sql.Date date) throws Exception {
        return new Date(date.getTime());
    }

}
