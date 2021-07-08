package com.happy.express.code;

import com.happy.express.persist.mysql.BaseGenerator;
import com.happy.express.persist.mysql.HappyTableGenerator;
import com.happy.express.persist.mysql.helper.BaseDatabaseHelper;
import com.happy.util.*;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: 基础代码生成器
 * @author: llw
 * @date: 2020-08-19
 */
public class BasicCodeMybatisGenerator extends BaseBasicCodeGenerator {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(BasicCodeMybatisGenerator.class);

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
        generateDaoMapper();
        //生成mapperXml
        generateMapperXml();
        //生成service
        generateServiceCode();
        //生成serviceImpl
        generateServiceImplCode();
    }

    /**
     * 生成dao接口
     * @throws Exception
     */
    public static void generateDaoMapper() throws Exception {
        generateInterface("daoMapper");
    }

    /**
     * 生成mapper.xml
     * @throws Exception
     */
    public static void generateMapperXml() throws Exception {
        Map<String, Class> entities = PackageReader.entities;
        String basePath = getMybatisMapperPath();
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdir();
        }
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String moduleName = CodeGeneratorHelper.getModuleName(classPackagePath);
            String dirPath = StringUtil.isEmpty(moduleName) ? basePath : "/" + moduleName;
            String fileName = clazz.getSimpleName() + "Mapper.xml";

            //判断是否存在mapper文件
            File mapperFile = new File(dirPath + "/" + fileName);
            if (mapperFile.exists()) continue;

            //生成模板
            Template template = configuration.getTemplate( "mapper.ftl");
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            FileOutputStream fos = new FileOutputStream(new File(dir, fileName));

            String daoPackagePath = getUserConfigBasePackagePath() + ".dao" + (moduleName != null ? "." + moduleName : "");
            String entityPackagePath = getUserConfigBasePackagePath() + ".entity" + (moduleName != null ? "." + moduleName : "");
            template.process(
                    CollectionUtil.generalMap()
                            .put("daoPackagePath", daoPackagePath)
                            .put("entityPackagePath", entityPackagePath)
                            .put("props", CodeGeneratorHelper.getPropertiesMappingList(clazz, true))
                            .put("propsWithoutId", CodeGeneratorHelper.getPropertiesMappingList(clazz, false))
                            .put("entityCols", CodeGeneratorHelper.generateEntityCols(clazz))
                            .put("insertCols", CodeGeneratorHelper.generateInsertCols(clazz))
                            .put("insertValues", CodeGeneratorHelper.generateInsertValues(clazz, true))
                            .put("insertValuesWithoutId", CodeGeneratorHelper.generateInsertValues(clazz, false))
                            .put("batchInsertValues", CodeGeneratorHelper.generateBatchInsertValues(clazz, true))
                            .put("batchInsertValuesWithoutId", CodeGeneratorHelper.generateBatchInsertValues(clazz, false))
                            .put("tableName", HappyTableGenerator.getTableName(clazz))
                            .put("wellNumberPre", "#{")
                            .put("wellNumberEnd", "}")
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
