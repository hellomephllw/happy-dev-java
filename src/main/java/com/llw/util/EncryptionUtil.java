package com.llw.util;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;

/**
 * @description: 字符串加密工具
 * @author: llw
 * @date: 2018-11-15
 */
public class EncryptionUtil {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(EncryptionUtil.class);

    /**DES来源字符集编码*/
    public static String DES_SOURCE_CHARSET = "UTF-8";
    /**DES结果字符集编码*/
    public static String DES_RESULT_CHARSET = "UTF-8";
    /**DES键*/
    public static String DES_KEY = "express-des-key";

    /**
     * 加密处理
     * @param inStr 加密的字符串
     * @param type  需要加密的格式 MD5 or SHA
     * @return 返回需要字符串
     */
    public static String encrypt(String inStr, String type) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance(type);
            byte[] digest = md.digest(inStr.getBytes());
            outStr = byteToString(digest);

            return outStr;
        } catch (Exception e) {
            logger.error("MD5或SHA加密发生错误", e);
            return null;
        }
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

    /**
     * DES加密
     * @param datasource 原数据
     * @param passedDesKey key
     * @return 加密字符串
     */
    public static String desEncode(String datasource, String passedDesKey) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(passedDesKey.getBytes(DES_SOURCE_CHARSET));
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, random);
            //现在，获取数据并加密
            byte[] temp = Base64.encodeBase64(cipher.doFinal(datasource.getBytes()));

            return IOUtils.toString(temp, DES_RESULT_CHARSET);
        } catch (Exception e) {
            logger.error("DES加密发生错误", e);
            return null;
        }
    }

    /**
     * DES解密
     * @param encode 加密数据
     * @param passedDesKey key
     * @return 解密数据
     */
    public static String desDecode(String encode, String passedDesKey) {
        try {
            //DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            //创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(passedDesKey.getBytes(DES_SOURCE_CHARSET));
            //创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //将DESKeySpec对象转换成SecretKey对象
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, secureKey, random);
            //真正开始解密操作
            return IOUtils.toString(cipher.doFinal(Base64.decodeBase64(encode)), DES_SOURCE_CHARSET);
        } catch (Exception e) {
            logger.error("DES解密发生错误", e);
            return null;
        }
    }

}
