package com.happy.util;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * @description: 数字工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class NumberUtil {

    /**
     * 生成随机整数
     * @param min 最小值
     * @param max 最大值
     * @return 随机整数
     * @throws Exception
     */
    public static long generateRandomIntegerNumber(int min, int max) {
        Random random = new Random();

        return random.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * 生成随机整数
     * @param count 位数
     * @return 随机整数
     * @throws Exception
     */
    public static long generateRandomIntegerNumber(int count) {
        long result = (long) (Math.random() * Math.pow(10, count));

        if (result < Math.pow(10, count - 1)) {
            result = NumberUtil.generateRandomIntegerNumber(count);
        }

        return result;
    }

    /**
     * 是数字
     * @param number 数字
     * @return 是否是数字
     */
    public static boolean isNumber(String number) {
        return !StringUtil.isEmpty(number) && RegexUtil.test("-?[0-9]+(\\.[0-9]+)?", number);
    }

    /**
     * 数字最多保留两位两位小数
     * @param number 数字
     * @return 最多保留两位小数(true)
     */
    public static boolean isMaxTwoDecimals(String number) {
        if (isNumber(number)) {
            return RegexUtil.test("-?[0-9]+(\\.[0-9]{1,2})?", number);
        }

        return false;
    }

    /**
     * 数字保留两位小数
     * @param number 数字
     * @return 保留两位小数(true)
     */
    public static boolean isKeepTwoDecimals(String number) {
        if (isNumber(number)) {
            return RegexUtil.test("-?[0-9]+(\\.[0-9]{2})?", number);
        }

        return false;
    }

    /**
     * 让数字保留两位小数(4舍5入)
     * @param number 数字
     * @return 字符串数字
     */
    public static String keepTwoDecimalsStringify(double number) {

        return new DecimalFormat("0.00").format(number);
    }

    /**
     * 让数字保留两位小数(4舍5入)
     * @param number 数字
     * @return 字符串数字
     * @throws Exception
     */
    public static String keepTwoDecimalsStringify(String number) throws Exception {
        if (!isNumber(number)) throw new Exception("传入字符串不是数字");

        return keepTwoDecimalsStringify(Double.parseDouble(number));
    }

    /**
     * 让数字保留两位小数(4舍5入)
     * @param number 数字
     * @return 数字
     */
    public static double keepTwoDecimals(double number) {

        return Double.parseDouble(new DecimalFormat("0.00").format(number));
    }

    /**
     * 让数字保留两位小数(4舍5入)
     * @param number 数字
     * @return 数字
     */
    public static double keepTwoDecimals(String number) throws Exception {
        if (!isNumber(number)) throw new Exception("传入字符串不是数字");

        return keepTwoDecimals(Double.parseDouble(number));
    }

    /**
     * 让数字保留两位小数(4舍5入, 舍去末尾的0)
     * @param number 数字
     * @return 字符串数字
     */
    public static String keepTwoDecimalsNoTailStringify(double number) {

        return new DecimalFormat("0.##").format(number);
    }

    /**
     * 让数字保留两位小数(4舍5入, 舍去末尾的0)
     * @param number 数字
     * @return 字符串数字
     */
    public static String keepTwoDecimalsNoTailStringify(String number) throws Exception {
        if (!isNumber(number)) throw new Exception("传入字符串不是数字");

        return keepTwoDecimalsNoTailStringify(Double.parseDouble(number));
    }

    /**
     * 让数字保留两位小数(4舍5入, 舍去末尾的0)
     * @param number 数字
     * @return 数字
     */
    public static double keepTwoDecimalsNoTail(double number) {

        return Double.parseDouble(new DecimalFormat("0.##").format(number));
    }

    /**
     * 让数字保留两位小数(4舍5入, 舍去末尾的0)
     * @param number 数字
     * @return 数字
     */
    public static double keepTwoDecimalsNoTail(String number) throws Exception {
        if (!isNumber(number)) throw new Exception("传入字符串不是数字");

        return keepTwoDecimalsNoTail(Double.parseDouble(number));
    }

    /**
     * 隐藏电话号码
     * @param mobile 电话号码
     * @return 电话号码
     * @throws Exception
     */
    public static String hideMobileNum(String mobile) throws Exception {
        if (StringUtil.isEmpty(mobile)) throw new Exception("传入电话不能为空");
        if (!isNumber(mobile)) throw new Exception("传入字符串不是数字");

        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    /**
     * 对象转为long
     * @param obj 对象
     * @return 结果
     * @throws Exception
     */
    public static Long objectToLong(Object obj) throws Exception {
        if (obj == null) throw new Exception("传入对象不能为空");

        if (obj instanceof Integer)
            return ((Integer) obj).longValue();

        return (Long) obj;
    }

}
