package com.happy.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: websocket事件处理器基类
 * @author: llw
 * @date: 2019-07-22
 */
public class BaseEvent {

    /**子类的class*/
    private Class subClazz = this.getClass();
    /**logger*/
    protected Logger logger;

    public BaseEvent() {
        logger = LoggerFactory.getLogger(subClazz);
    }

}
