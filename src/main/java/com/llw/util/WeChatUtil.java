package com.llw.util;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @discription: 微信工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class WeChatUtil {

    /**
     * 获取nonce str随机数
     * @return 随机数
     * @throws Exception
     */
    public static String getNonceStr() throws Exception {
        return ThreadLocalRandom.current().nextInt(89999999) + 10000000 + "";
    }

    /**
     * 获取时间戳
     * @return 时间戳
     * @throws Exception
     */
    public static long getTimeStamp() throws Exception {
        return new Date().getTime();
    }

    /**
     * 微信支付签名
     * @return 签名
     * @throws Exception
     */
    public static String getPaySign(String appId, String mchId, String deviceInfo, String body, String nonceStr, String key) throws Exception {
        // 非空参数值的参数按照参数名ASCII码从小到大排序
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", appId);
        parameters.put("mch_id", mchId);
        parameters.put("device_info", deviceInfo);
        parameters.put("body", body);
        parameters.put("nonce_str", nonceStr);

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
        stringBuilder.append("key=" + key);

        //md5加密
        return EncryptionUtil.encrypt(stringBuilder.toString(), "MD5");
    }

}
