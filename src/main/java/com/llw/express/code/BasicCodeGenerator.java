package com.llw.express.code;

import com.llw.util.FileUtil;
import com.llw.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @discription: 基础代码生成器
 * @author: llw
 * @date: 2018-11-21
 */
public class BasicCodeGenerator {

    /**log*/
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**源码路径, 对根路径的补充*/
    private final static String _BASE_SOURCE_CODE_PATH = "/src/main/java/";
    /**用户配置的包路径*/
    private static String _RELATIVE_PACKAGE_PATH;

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
    }

    /**
     * 初始化路径
     * @param userConfigRelativePackagePath 用户配置的包路径
     * @throws Exception
     */
    private static void initPath(String userConfigRelativePackagePath) throws Exception {
        _RELATIVE_PACKAGE_PATH = userConfigRelativePackagePath.replaceAll("\\.", "/");
    }

    /**
     * 获取包的基本路径
     * @return 包路径
     * @throws Exception
     */
    public static String getBasePackagePath() throws Exception {
        if (getRelativePackagePath() == null) throw new Exception("用户没有配置包路径");

        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH + getRelativePackagePath();
    }

    /**
     * 获取构建的class路径
     * @return class路径
     * @throws Exception
     */
    public static String getBuildClassPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + "/build/classes/java/main";
    }

    /**
     * 获取用户配置包路径
     * @return 用户配置包路径
     * @throws Exception
     */
    public static String getRelativePackagePath() throws Exception {
        return _RELATIVE_PACKAGE_PATH;
    }

    public static void main(String[] args) {
        try {
            //生成基础代码
            generate(args[0]);
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.printStackTrace(logger, e);
        }
    }

}
