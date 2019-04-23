package com.happy.base;

import com.happy.exception.BusinessException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @description: controller基类
 * @author: happy
 * @date: 2016-11-18
 */
public abstract class BaseController {

    @ExceptionHandler
    public ModelAndView errorPage(HttpServletRequest request, Exception exception) {

        System.out.println("抛出异常！！！！！");
        System.out.println(exception.getMessage());
        for (StackTraceElement ste : exception.getStackTrace()) {
            System.out.println(ste.toString());
        }

        if (exception instanceof MissingServletRequestParameterException) {
            // TODO: 2016/11/26
        } else if (exception instanceof BusinessException) {
            // TODO: 2016/11/26
        }

        return new ModelAndView("error");
    }

}
