package com.happy.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 服务实现累基类
 * @author: llw
 * @date: 2019-07-22
 */
public class BaseService {

    /**子类的class*/
    private Class subClazz = this.getClass();
    /**logger*/
    protected Logger logger;

    public BaseService() {
        logger = LoggerFactory.getLogger(subClazz);
    }

}
