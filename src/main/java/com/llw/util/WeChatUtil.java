package com.llw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @description: 微信工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class WeChatUtil {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(WeChatUtil.class);

    /**
     * 获取nonce str随机数
     * @return 随机数
     * @throws Exception
     */
    public static String getNonceStr() {
        return ThreadLocalRandom.current().nextInt(89999999) + 10000000 + "";
    }

    /**
     * 获取时间戳
     * @return 时间戳
     * @throws Exception
     */
    public static long getTimeStamp() {
        return new Date().getTime();
    }

    /**
     * 微信支付签名
     * @return 签名
     * @throws Exception
     */
    public static String getPaySign(Map<String, Object> paramsMap) throws Exception {
        // 非空参数值的参数按照参数名ASCII码从小到大排序
        SortedMap<Object, Object> parameters = new TreeMap<>();
        for (String key : paramsMap.keySet()) {
            parameters.put(key, paramsMap.get(key));
        }

        StringBuilder stringBuilder = new StringBuilder();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                stringBuilder.append(k + "=" + v + "&");
            }
        }
        stringBuilder.append("key=" + parameters.get("key"));

        logger.info("组装参数如下: ");
        logger.info(stringBuilder.toString());

        //md5加密
        return EncryptionUtil.encrypt(stringBuilder.toString(), "MD5").toUpperCase();
    }

}
