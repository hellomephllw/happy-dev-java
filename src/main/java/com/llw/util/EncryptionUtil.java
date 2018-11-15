package com.llw.util;

import java.security.MessageDigest;

/**
 * @discription: 字符串加密工具
 * @author: llw
 * @date: 2018-11-15
 */
public class EncryptionUtil {

    /**
     * 加密处理
     * @param inStr 加密的字符串
     * @param type 需要加密的格式 MD5 or SHA
     * @return 返回需要字符串
     */
    public static String encrypt(String inStr, String type) throws Exception {
        MessageDigest md = null;
        String outStr = null;
        md = MessageDigest.getInstance(type);
        byte[] digest = md.digest(inStr.getBytes());
        outStr = byteToString(digest);

        return outStr;
    }

    private static String byteToString(byte[] digest) throws Exception {
        String str = "";
        String tempStr = "";
        for (int i = 0; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xFF));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            } else {
                str = str + tempStr;
            }
        }

        return str.toLowerCase();
    }

}
