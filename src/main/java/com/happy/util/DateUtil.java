package com.happy.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * @description: 日期工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class DateUtil {

    /** 毫秒 */
    public final static long MS = 1;
    /** 每秒钟的毫秒数 */
    public final static long SECOND_MS = MS * 1000;
    /** 每分钟的毫秒数 */
    public final static long MINUTE_MS = SECOND_MS * 60;
    /** 每小时的毫秒数 */
    public final static long HOUR_MS = MINUTE_MS * 60;
    /** 每天的毫秒数 */
    public final static long DAY_MS = HOUR_MS * 24;

    /** 标准日期格式 */
    public final static String NORM_DATE_PATTERN = "yyyy-MM-dd";
    /** 标准时间格式 */
    public final static String NORM_TIME_PATTERN = "HH:mm:ss";
    /** 标准日期时间格式 */
    public final static String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /** HTTP头中日期时间格式 */
    public final static String HTTP_DATETIME_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * 获取一个很老的时期(2000-01-01 00:00:00)，用作默认值
     * @return 老日期
     */
    public static Date oldDate() {
        Calendar oldDate = Calendar.getInstance();
        oldDate.set(2000, 0, 1, 0, 0, 0);

        return oldDate.getTime();
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return formatDateTime(new Date());
    }

    /**
     * 当前日期，格式 yyyy-MM-dd
     * @return 当前日期的标准形式字符串
     */
    public static String today() {
        return formatDate(new Date());
    }

    /**
     * 根据特定格式格式化日期
     * @param date 被格式化的日期
     * @param format 格式
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format){
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式 yyyy-MM-dd HH:mm:ss
     * @param date 被格式化的日期
     * @return 格式化后的日期
     */
    public static String formatDateTime(Date date) {
        return new SimpleDateFormat(NORM_DATETIME_PATTERN).format(date);
    }

    /**
     * 格式化为Http的标准日期格式
     * @param date 被格式化的日期
     * @return HTTP标准形式日期字符串
     */
    public static String formatHttpDate(Date date) {
        return new SimpleDateFormat(HTTP_DATETIME_PATTERN).format(date);
    }

    /**
     * 格式 yyyy-MM-dd
     * @param date 被格式化的日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        return new SimpleDateFormat(NORM_DATE_PATTERN).format(date);
    }

    /**
     * 将特定格式的日期转换为Date对象
     * @param dateString 特定格式的日期
     * @param format 格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static Date parse(String dateString, String format) throws Exception {
        return (new SimpleDateFormat(format)).parse(dateString);
    }

    /**
     * 格式yyyy-MM-dd HH:mm:ss
     * @param dateString 标准形式的时间字符串
     * @return 日期对象
     */
    public static Date parseDateTime(String dateString) throws Exception {
        return new SimpleDateFormat(NORM_DATETIME_PATTERN).parse(dateString);
    }

    /**
     * 格式yyyy-MM-dd
     * @param dateString 标准形式的日期字符串
     * @return 日期对象
     */
    public static Date parseDate(String dateString) throws Exception {
        return new SimpleDateFormat(NORM_DATE_PATTERN).parse(dateString);
    }

    /**
     * 格式HH:mm:ss
     * @param dateString 标准形式的日期字符串
     * @return 日期对象
     */
    public static Date parseTime(String dateString) throws Exception {
        return new SimpleDateFormat(NORM_TIME_PATTERN).parse(dateString);
    }

    /**
     * 格式：<br>
     * 1、yyyy-MM-dd HH:mm:ss<br>
     * 2、yyyy-MM-dd<br>
     * 3、HH:mm:ss>
     * @param dateStr 日期字符串
     * @return 日期
     */
    public static Date parse(String dateStr) throws Exception {
        int length = dateStr.length();

        if (length == DateUtil.NORM_DATETIME_PATTERN.length()) {
            return parseDateTime(dateStr);
        } else if (length == DateUtil.NORM_DATE_PATTERN.length()) {
            return parseDate(dateStr);
        } else if (length == DateUtil.NORM_TIME_PATTERN.length()) {
            return parseTime(dateStr);
        }

        return null;
    }

    /**
     * 昨天
     * @return 昨天
     */
    public static Date yesterday() {
        return offsetDate(new Date(), Calendar.DAY_OF_YEAR, -1);
    }

    /**
     * 上周
     * @return 上周
     */
    public static Date lastWeek() {
        return offsetDate(new Date(), Calendar.WEEK_OF_YEAR, -1);
    }

    /**
     * 上个月
     * @return 上个月
     */
    public static Date lastMonth() {
        return offsetDate(new Date(), Calendar.MONTH, -1);
    }

    /**
     * 获取指定日期偏移指定时间后的时间
     * @param date 基准日期
     * @param calendarField 偏移的粒度大小（小时、天、月等）使用Calendar中的常数
     * @param offset 偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static Date offsetDate(Date date, int calendarField, int offset){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarField, offset);
        return cal.getTime();
    }

    /**
     * 判断两个日期相差的时长<br/>
     * 返回 minuend - subtrahend 的差
     * @param subtrahend 减数日期
     * @param minuend 被减数日期
     * @param diffField 相差的选项：相差的天、小时
     * @return 日期差
     */
    public static long diff(Date subtrahend, Date minuend, long diffField) {
        long diff = minuend.getTime() - subtrahend.getTime();
        return diff / diffField;
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：纳秒
     * @param preTime 之前记录的时间
     * @return 时间差，纳秒
     */
    public static long spendNt(long preTime) {
        return System.nanoTime() - preTime;
    }

    /**
     * 计时，常用于记录某段代码的执行时间，单位：毫秒
     * @param preTime 之前记录的时间
     * @return 时间差，毫秒
     */
    public static long spendMs(long preTime) {
        return System.currentTimeMillis() - preTime;
    }

    /**
     * 把util date转换为sql date
     * @param date util date
     * @return sql date
     * @throws Exception
     */
    public static java.sql.Date transformUtilDateToSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 把sql date转换为util date
     * @param date sql date
     * @return util date
     * @throws Exception
     */
    public static Date transformSqlDateToUtilDate(java.sql.Date date) {
        return new Date(date.getTime());
    }

    /**
     * 把str日期类型变为Date类型
     * @param dateStr 时间字符串
     * @return 时间
     * @throws Exception
     */
    public static Date transferStr2DateWithValidate(String dateStr) {
        try {
            return DateUtil.parseDate(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("时间格式只能是yyyy-MM-dd");
        }
    }

    /**
     * 把str日期类型变为Date类型
     * @param dateStr 时间字符串
     * @return 时间
     * @throws Exception
     */
    public static Date transferStr2DateTimeWithValidate(String dateStr) {
        try {
            return DateUtil.parseDateTime(dateStr);
        } catch (Exception e) {
            throw new RuntimeException("时间格式只能是yyyy-MM-dd HH:mm:ss");
        }
    }

}
