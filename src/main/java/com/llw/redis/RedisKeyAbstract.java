package com.llw.redis;

import com.llw.util.KeyGenerateUtil;

/**
 * @description: key管理工具基类
 * @author: llw
 * @date: 2018-11-27
 */
public abstract class RedisKeyAbstract {

    /**
     * 生成工具
     * @param module 模块名
     * @param name 名称
     * @return key
     */
    protected static String generate(String module, String name) {
        return module + "." + name + "." + KeyGenerateUtil.getUuidKey();
    }

}
