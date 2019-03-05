package com.llw.exception;

/**
 * @description: 业务异常
 * @author: llw
 * @date: 2016-11-26
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
