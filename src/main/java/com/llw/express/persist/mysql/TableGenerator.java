package com.llw.express.persist.mysql;

import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 表生成器
 * @author: llw
 * @date: 2018-11-22
 */
public class TableGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(TableGenerator.class);

    /**
     * 生成表格
     * @param env 环境变量
     * @throws Exception
     */
    public static void generate(String env) throws Exception {
        DatabaseHelper.connectDatabase(env);
    }

    public static void main(String[] args) {
        try {
            //生成表格
            generate(args[0]);
        } catch (Exception e) {
            LoggerUtil.printStackTrace(logger, e);
        }
    }

}
