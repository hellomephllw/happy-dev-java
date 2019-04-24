package com.happy.exception;

/**
 * @description: 业务异常
 * @author: llw
 * @date: 2019-03-05
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}
