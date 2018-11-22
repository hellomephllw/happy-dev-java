package com.llw.exception;

/**
 * @description: 业务异常
 * @author: llw
 * @date: 2016-11-26
 */
public class BussinessException extends RuntimeException {

    public BussinessException(String message) {
        super(message);
    }

}
