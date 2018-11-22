package com.llw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @description: 网络工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class NetUtil {

    /**log*/
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 发送http请求
     * @param attrs 请求的参数
     * @return 响应内容
     * @throws Exception
     */
    public static String sendHttpRequest(Map<String, Object> attrs) {
        String url = (String) attrs.get("url");
        String method = attrs.get("type") == null ? "GET" : ((String) attrs.get("type")).toUpperCase();
        String contentType = attrs.get("contentType") == null ? "application/json" : (String) attrs.get("contentType");

        HttpURLConnection conn = null;
        try {
            URL serverUrl = new URL(url);
            conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-type", contentType);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
        } catch (Exception e) {
            logger.info("发送请求失败, url: " + attrs.get("url") + ", msg: " + e.getMessage());
            LoggerUtil.printStackTrace(logger, e);
        }

        logger.info("发出请求: " + attrs.get("url"));

        return parseResult(conn);
    }

    /**
     * 解析请求响应流为字符串
     * @param connection socket连接
     * @return 响应
     * @throws Exception
     */
    private static String parseResult(HttpURLConnection connection) {
        StringBuilder stringBuilder = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str);
            }
        } catch (Exception e) {
            logger.info("接收响应内容失败, msg: " + e.getMessage());
            LoggerUtil.printStackTrace(logger, e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                logger.info("关闭接收响应的流失败, msg: " + e.getMessage());
                LoggerUtil.printStackTrace(logger, e);
            }
        }

        logger.info("响应内容: " + stringBuilder.toString());

        return stringBuilder.toString();
    }

}
