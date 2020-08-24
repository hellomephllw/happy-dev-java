package com.happy.express.code;

import com.happy.util.CollectionUtil;
import com.happy.util.DateUtil;
import com.happy.util.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * @description: 基础代码生成器基类
 * @author: llw
 * @date: 2020-08-19
 */
public class BaseBasicCodeGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(BaseBasicCodeGenerator.class);

    /**freemarker*/
    protected final static Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

    /**源码路径, 对根路径的补充*/
    protected final static String _BASE_SOURCE_CODE_PATH = "/src/main/java/";
    /**用户配置的包路径*/
    protected static String _USER_CONFIG_BASE_PACKAGE_FILE_PATH;

    static {
        configuration.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/templates/freemarker/code"));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setCacheStorage(NullCacheStorage.INSTANCE);
    }

    /**
     * 初始化路径
     * @param userConfigRelativePackagePath 用户配置的包路径
     * @throws Exception
     */
    protected static void initPath(String userConfigRelativePackagePath) throws Exception {
        _USER_CONFIG_BASE_PACKAGE_FILE_PATH = userConfigRelativePackagePath.replaceAll("\\.", "/");
    }

    /**
     * 获取用户提供的包的基本路径
     * @return 包路径
     * @throws Exception
     */
    public static String getBasePackagePath() throws Exception {
        if (getUserConfigBasePackageFilePath() == null) throw new Exception("用户没有配置包路径");

        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH + getUserConfigBasePackageFilePath();
    }

    /**
     * 获取用户项目构建的class路径
     * @return class路径
     * @throws Exception
     */
    public static String getBuildClassPath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + "/build/classes/java/main";
    }

    /**
     * 获取用户项目源码路径
     * @return 源码路径
     * @throws Exception
     */
    public static String getSourceCodePath() throws Exception {
        return FileUtil.getLocalRootAbsolutePath() + _BASE_SOURCE_CODE_PATH.substring(0, _BASE_SOURCE_CODE_PATH.length() - 1);
    }

    /**
     * 获取用户配置包的文件路径
     * @return 用户配置包的文件路径
     * @throws Exception
     */
    public static String getUserConfigBasePackageFilePath() throws Exception {
        return _USER_CONFIG_BASE_PACKAGE_FILE_PATH;
    }

    /**
     * 获取用户配置包路径
     * @return 用户配置包路径
     * @throws Exception
     */
    public static String getUserConfigBasePackagePath() throws Exception {
        return _USER_CONFIG_BASE_PACKAGE_FILE_PATH.replaceAll("/", ".");
    }

    /**
     * 生成service或dao接口
     * @param type 接受dao或service
     * @throws Exception
     */
    protected static void generateInterface(String type) throws Exception {
        String templateFileName = null;
        if (type.equals("daoMapper")) {
            templateFileName = type;
            type = "dao";
        }
        String typeUpper = type.substring(0, 1).toUpperCase() + type.substring(1);
        String typeLower = type.substring(0, 1).toLowerCase() + type.substring(1);
        if (templateFileName == null) {
            templateFileName = typeLower;
        }

        Map<String, Class> entities = PackageReader.entities;
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String dirPath = getSourceCodePath() + "/" + getUserConfigBasePackageFilePath() + "/" + typeLower;
            String fileName = "I" + clazz.getSimpleName() + typeUpper + ".java";
            String moduleName = null;

            String[] fragments = classPackagePath.split("entity")[1].split("\\.");
            if (fragments.length == 3) {
                //子模块
                moduleName = fragments[1];
                dirPath += "/" + moduleName;
            }

            //判断是否存在该接口
            File daoFile = new File(dirPath + "/" + fileName);
            if (daoFile.exists()) continue;

            //生成模板
            Template template = configuration.getTemplate(templateFileName + ".ftl");
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));

            String packagePath = getUserConfigBasePackagePath() + "." + typeLower + (moduleName != null ? "." + moduleName : "");
            template.process(
                    CollectionUtil.stringMap()
                            .put("packagePath", packagePath)
                            .put("entitySourceCodePath", classPackagePath)
                            .put("author", System.getProperty("user.name"))
                            .put("date", DateUtil.today())
                            .put("entityClassName", clazz.getSimpleName())
                            .put("entityInstanceName", clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1))
                            .build(),
                    new BufferedWriter(new OutputStreamWriter(fos, "utf-8"),10240));
        }
    }

    /**
     * 生成dao和service的接口实现类
     * @param type 接受dao或service
     * @throws Exception
     */
    protected static void generateInterfaceImplement(String type) throws Exception {
        String typeUpper = type.substring(0, 1).toUpperCase() + type.substring(1);
        String typeLower = type.substring(0, 1).toLowerCase() + type.substring(1);

        Map<String, Class> entities = PackageReader.entities;
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String dirPath = getSourceCodePath() + "/" + getUserConfigBasePackageFilePath() + "/" + typeLower;
            String fileName = "I" + clazz.getSimpleName() + typeUpper + "Impl.java";
            String moduleName = null;

            String[] fragments = classPackagePath.split("entity")[1].split("\\.");
            if (fragments.length == 3) {
                //子模块
                moduleName = fragments[1];
                dirPath += "/" + moduleName;
            }
            dirPath += "/impl";

            //判断是否存在dao实现类
            File daoFile = new File(dirPath + "/" + fileName);
            if (daoFile.exists()) continue;

            //生成模板
            Template template = configuration.getTemplate(typeLower + "Impl.ftl");
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));

            String packagePath = getUserConfigBasePackagePath() + "." + typeLower + (moduleName != null ? "." + moduleName : "") + ".impl";
            String daoClassPackagePath = getUserConfigBasePackagePath() + ".dao" + (moduleName != null ? "." + moduleName : "") + ".I" + clazz.getSimpleName() + "Dao";
            String serviceClassPackagePath = getUserConfigBasePackagePath() + ".service" + (moduleName != null ? "." + moduleName : "") + ".I" + clazz.getSimpleName() + "Service";
            template.process(
                    CollectionUtil.stringMap()
                            .put("packagePath", packagePath)
                            .put("daoClassPackagePath", daoClassPackagePath)
                            .put("serviceClassPackagePath", serviceClassPackagePath)
                            .put("entitySourceCodePath", classPackagePath)
                            .put("author", System.getProperty("user.name"))
                            .put("date", DateUtil.today())
                            .put("entityClassName", clazz.getSimpleName())
                            .put("entityInstanceName", clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1))
                            .build(),
                    new BufferedWriter(new OutputStreamWriter(fos, "utf-8"),10240));
        }
    }

}
