package com.happy.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: controller基类
 * @author: llw
 * @date: 2016-11-18
 */
public abstract class BaseController {

    /**子类的class*/
    private Class subClazz = this.getClass();
    /**logger*/
    protected Logger logger;

    public BaseController() {
        logger = LoggerFactory.getLogger(subClazz);
    }

}
