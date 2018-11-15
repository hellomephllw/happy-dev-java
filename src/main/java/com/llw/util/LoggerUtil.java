package com.llw.util;

import org.slf4j.Logger;

/**
 * @discription: 日志工具类
 * @author: llw
 * @date: 2018-11-05
 */
public class LoggerUtil {

    /**
     * 在日志中记录详细的错误信息
     * @param logger logger
     * @param e 异常
     */
    public static void printStackTrace(Logger logger, Exception e) {
        logger.error(e.getMessage());
        for (StackTraceElement ste : e.getStackTrace()) {
            logger.error(ste.toString());
        }
    }

}
