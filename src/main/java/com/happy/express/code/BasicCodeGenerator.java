package com.happy.express.code;

import com.happy.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 基础代码生成器
 * @author: llw
 * @date: 2018-11-21
 */
public class BasicCodeGenerator extends BaseBasicCodeGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(BasicCodeGenerator.class);

    /**
     * 生成基础代码
     * @param userConfigRelativePackagePath 用户配置的包路径
     * @throws Exception
     */
    public static void generate(String userConfigRelativePackagePath) throws Exception {
        //初始化路径
        initPath(userConfigRelativePackagePath);
        //读取所有实体
        PackageReader.readAllEntities(getBasePackagePath());
        //读取所有dao
        PackageReader.readAllDaos(getBasePackagePath());
        //读取所有service
        PackageReader.readAllServices(getBasePackagePath());
        //生成dao
        generateDaoCode();
        //生成daoImpl
        generateDaoImplCode();
        //生成service
        generateServiceCode();
        //生成serviceImpl
        generateServiceImplCode();
    }

    /**
     * 生成dao接口
     * @throws Exception
     */
    public static void generateDaoCode() throws Exception {
        generateInterface("dao");
    }

    /**
     * 生成dao实现类
     * @throws Exception
     */
    public static void generateDaoImplCode() throws Exception {
        generateInterfaceImplement("dao");
    }

    /**
     * 生成服务接口
     * @throws Exception
     */
    public static void generateServiceCode() throws Exception {
        generateInterface("service");
    }

    /**
     * 生成服务实现类
     * @throws Exception
     */
    public static void generateServiceImplCode() throws Exception {
        generateInterfaceImplement("service");
    }

    public static void main(String[] args) {
        try {
            //生成基础代码
            generate(args[0]);
        } catch (Exception e) {
            LoggerUtil.printStackTrace(logger, e);
        }
    }

}
