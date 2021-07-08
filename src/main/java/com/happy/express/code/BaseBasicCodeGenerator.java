package com.happy.express.code;

import com.happy.util.CollectionUtil;
import com.happy.util.DateUtil;
import com.happy.util.FileUtil;
import com.happy.util.StringUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
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
public class BaseBasicCodeGenerator extends BasePathResolver {

    /**freemarker*/
    protected final static Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);

    static {
        configuration.setTemplateLoader(new ClassTemplateLoader(FreeMarkerTemplateUtils.class, "/templates/freemarker/code"));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setCacheStorage(NullCacheStorage.INSTANCE);
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
        String typeUpper = CodeGeneratorHelper.makeFirstLetterUpper(type);
        String typeLower = CodeGeneratorHelper.makeFirstLetterLower(type);
        if (templateFileName == null) {
            templateFileName = typeLower;
        }

        Map<String, Class> entities = PackageReader.entities;
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String dirPath = getSourceCodePath() + "/" + getUserConfigBasePackageFilePath() + "/" + typeLower;
            String fileName = clazz.getSimpleName() + typeUpper + ".java";
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
        String typeUpper = CodeGeneratorHelper.makeFirstLetterUpper(type);
        String typeLower = CodeGeneratorHelper.makeFirstLetterLower(type);

        Map<String, Class> entities = PackageReader.entities;
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String moduleName = CodeGeneratorHelper.getModuleName(classPackagePath);
            String dirPath = getSourceCodePath() + "/" + getUserConfigBasePackageFilePath() + "/" + typeLower + (!StringUtil.isEmpty(moduleName) ? "/" + moduleName : "") + "/impl";
            String fileName = clazz.getSimpleName() + typeUpper + "Impl.java";

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

            String packagePath = getUserConfigBasePackagePath() + "." + typeLower + (!StringUtil.isEmpty(moduleName) ? "." + moduleName : "") + ".impl";
            String daoClassPackagePath = getUserConfigBasePackagePath() + ".dao" + (!StringUtil.isEmpty(moduleName) ? "." + moduleName : "") + "." + clazz.getSimpleName() + "Dao";
            String serviceClassPackagePath = getUserConfigBasePackagePath() + ".service" + (!StringUtil.isEmpty(moduleName) ? "." + moduleName : "") + "." + clazz.getSimpleName() + "Service";
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
