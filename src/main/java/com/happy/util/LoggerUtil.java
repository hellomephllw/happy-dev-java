package com.happy.util;

import org.slf4j.Logger;

/**
 * @description: 日志工具类
 * @author: happy
 * @date: 2018-11-05
 */
public class LoggerUtil {

    /**
     * 在日志中记录详细的错误信息
     * @param logger logger
     * @param e 异常
     */
    public static void printStackTrace(Logger logger, Exception e) {
        logger.error("发生错误", e);
    }

}
