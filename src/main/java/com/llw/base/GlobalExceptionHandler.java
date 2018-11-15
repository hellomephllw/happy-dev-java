package com.llw.base;

import com.llw.exception.BussinessException;
import com.llw.util.FileUtil;
import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @discription: spring mvc的controller异常统一处理
 * @author: llw
 * @date: 2018-10-23
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultVo defaultExceptionHandler(Exception e) {

        if (e instanceof BussinessException) {
            return new ResultVo<>(0, e.getMessage(), null);
        } else if (e instanceof MissingServletRequestParameterException) {
            return new ResultVo<>(0, "缺少参数", null);
        }

        LoggerUtil.printStackTrace(logger, e);

        return new ResultVo<>(0, "服务器繁忙", null);
    }

}
