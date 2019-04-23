package com.happy.exception;

import com.happy.base.ResultVo;
import com.happy.util.LoggerUtil;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 全局异常处理器
 * @author: happy
 * @date: 2018-11-15
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVo defaultExceptionHandler(Exception e) {

        logger.error(">>>>>>>>>出现异常");

        if (e instanceof BusinessException) {
            logger.error(e.getMessage());
            return new ResultVo<>(0, e.getMessage(), null);
        } else if (e instanceof MissingServletRequestParameterException) {
            logger.error(e.getMessage());
            return new ResultVo<>(0, "缺少参数", null);
        } else if (e instanceof StaleObjectStateException) {
            return new ResultVo<>(0, "操作失败，有数据存在并发安全问题", null);
        }

        LoggerUtil.printStackTrace(logger, e);

        return new ResultVo<>(0, "服务器繁忙", null);
    }

}
