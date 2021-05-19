package com.happy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public final static String CONTENT_TYPE_JSON = "application/json";
    public final static String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public final static String CONTENT_TYPE_FILE = "multipart/form-data";

    /**
     * 发送http请求
     * @param attrs 请求的参数
     * @return 响应内容
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String sendHttpRequest(Map<String, Object> attrs) {
        String url = (String) attrs.get("url");
        String method = attrs.get("type") == null ? "GET" : ((String) attrs.get("type")).toUpperCase();
        String contentType = attrs.get("contentType") == null ? CONTENT_TYPE_JSON + ";charset=UTF-8" : (String) attrs.get("contentType");
        Map<String, String> headers = (Map<String, String>) attrs.get("headers");

        HttpURLConnection conn = null;
        try {
            URL serverUrl = new URL(url);
            conn = (HttpURLConnection) serverUrl.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-type", contentType);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.setRequestProperty(header.getKey(), header.getValue());
            }
            conn.setInstanceFollowRedirects(false);
            if (attrs.get("body") != null && attrs.get("body") instanceof String)
                setRequestBody(conn, (String) attrs.get("body"));

            conn.connect();
            logger.info("发出请求: " + attrs.get("url"));
            logger.info("请求参数: " + attrs.get("body"));

            if (conn.getResponseCode() == 200) {
                return parseResult(conn);
            }
            return null;
        } catch (Exception e) {
            logger.error("发送请求失败, url: " + attrs.get("url"), e);
            return null;
        } finally {
            if (conn != null) conn.disconnect();
        }

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
            logger.error("接收响应内容失败", e);
        } finally {
            try {
                if (bufferedReader != null) bufferedReader.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (inputStream != null) inputStream.close();
            } catch (Exception e) {
                logger.error("关闭接收响应的流失败", e);
            }
        }

        logger.info("响应内容: " + stringBuilder.toString());

        return stringBuilder.toString();
    }

    /**
     * 设置请求消息体
     * @param connection socket连接
     * @param body 消息体内容
     */
    private static void setRequestBody(HttpURLConnection connection, String body) {
        if (body != null && !"".equals(body)) {
            connection.setDoOutput(true);
            OutputStream os = null;
            DataOutputStream writer = null;
            try {
                byte[] writeBytes = body.getBytes("UTF-8");
                connection.setRequestProperty("Content-Length", String.valueOf(writeBytes.length));

                os = connection.getOutputStream();
                writer = new DataOutputStream(os);
                writer.write(writeBytes);
            } catch (IOException e) {
                logger.error("发送请求消息题失败", e);
            } finally {
                try {
                    if (writer != null) writer.close();
                    if (os != null) os.close();
                } catch (IOException e) {
                    logger.error("关闭发送请求消息体的流失败", e);
                }
            }
        }
    }

}
