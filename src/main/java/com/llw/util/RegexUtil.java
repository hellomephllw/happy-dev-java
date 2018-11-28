package com.llw.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: 正则表达式工具类
 * @author: llw
 * @date: 2018-10-24
 */
public class RegexUtil {

    /**手机号码正则*/
    public final static String _REGEX_MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])|(18[0-9])|(19[8,9]))\\d{8}$";
    /**密码正则*/
    public final static String _REGEX_PASSWROD = "^[A-Za-z0-9]{6,15}$";
    /**邮箱正则*/
    public final static String _REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 判断内容是否满足正则(必须完全匹配)
     * @param regex 正则字符串
     * @param content 内容
     * @return 是否满足
     * @throws Exception
     */
    public static boolean test(String regex, String content) {
        return match(regex, content);
    }

    /**
     * 判断内容是否满足正则(必须完全匹配)
     * @param regex 正则字符串
     * @param content 内容
     * @return 是否满足
     * @throws Exception
     */
    public static boolean match(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        return matcher.matches();
    }

    /**
     * 判断内容是否满足正则(部分或完全匹配)
     * @param regex 正则字符串
     * @param content 内容
     * @return 是否满足
     */
    public static boolean find(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        return matcher.find();
    }

}
