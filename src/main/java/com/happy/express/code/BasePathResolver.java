package com.happy.express.code;

import com.happy.util.FileUtil;

import java.io.File;

/**
 * @description: 路径解析器
 * @author: liliwen
 * @date: 2021-07-08
 */
public class BasePathResolver {

    /**源码路径, 对根路径的补充*/
    protected final static String _BASE_SOURCE_CODE_PATH = "/src/main/java";
    /**class文件路径*/
    protected final static String _BASE_CLASS_CODE_PATH = "/build/classes/java/main";
    /**mybatis mapper路径*/
    protected final static String _BASE_MYBATIS_MAPPER_PATH = "/src/main/resources/mapper";

    /**用户配置的包路径*/
    protected static String _USER_CONFIG_BASE_PACKAGE_FILE_PATH;

    /**
     * 初始化路径
     * @param userConfigRelativePackagePath 用户配置的包路径
     */
    protected static void initPath(String userConfigRelativePackagePath) {
        _USER_CONFIG_BASE_PACKAGE_FILE_PATH = userConfigRelativePackagePath.replaceAll("\\.", "/");
    }

    /**
     * 获取用户提供的包的基本路径
     * @return 包路径
     */
    public static String getBasePackagePath() {
        if (getUserConfigBasePackageFilePath() == null) throw new RuntimeException("用户没有配置包路径");

        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH + File.separator + getUserConfigBasePackageFilePath();
    }

    /**
     * 获取用户项目构建的class路径
     * @return class路径
     */
    public static String getBuildClassPath() {
        return FileUtil.getLocalRootAbsolutePath() + _BASE_CLASS_CODE_PATH;
    }

    /**
     * 获取用户项目源码路径
     * @return 源码路径
     */
    public static String getSourceCodePath() {
        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH;
    }

    /**
     * 获取用户项目mybatis mapper路径
     * @return mapper路径
     */
    public static String getMybatisMapperPath() {
        return FileUtil.getLocalRootAbsolutePath() + _BASE_MYBATIS_MAPPER_PATH;
    }

    /**
     * 获取用户配置包的文件路径
     * @return 用户配置包的文件路径
     */
    public static String getUserConfigBasePackageFilePath() {
        return _USER_CONFIG_BASE_PACKAGE_FILE_PATH;
    }

    /**
     * 获取用户配置包路径
     * @return 用户配置包路径
     */
    public static String getUserConfigBasePackagePath() {
        return _USER_CONFIG_BASE_PACKAGE_FILE_PATH.replaceAll("/", ".");
    }

}
