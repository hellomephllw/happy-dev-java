package com.happy.express.code;

import com.happy.express.persist.mysql.BaseGenerator;
import com.happy.express.persist.mysql.HappyTableGenerator;
import com.happy.express.persist.mysql.helper.BaseDatabaseHelper;
import com.happy.util.CollectionUtil;
import com.happy.util.DateUtil;
import com.happy.util.FileUtil;
import com.happy.util.LoggerUtil;
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
        String basePath = FileUtil.getLocalRootAbsolutePath() + "/src/main/resources/mapper";
        File file = new File(basePath);
        if (!file.exists()) {
            file.mkdir();
        }
        for (String classPackagePath : entities.keySet()) {
            Class clazz = entities.get(classPackagePath);

            String dirPath = basePath;
            String fileName = clazz.getSimpleName() + "Mapper.xml";
            String moduleName = null;

            String[] fragments = classPackagePath.split("entity")[1].split("\\.");
            if (fragments.length == 3) {
                //子模块
                moduleName = fragments[1];
                dirPath += "/" + moduleName;
            }

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
                            .put("props", getPropertiesMappingList(clazz, true))
                            .put("propsWithoutId", getPropertiesMappingList(clazz, false))
                            .put("entityCols", generateEntityCols(clazz))
                            .put("insertCols", generateInsertCols(clazz))
                            .put("insertValues", generateInsertValues(clazz))
                            .put("batchInsertValues", generateBatchInsertValues(clazz))
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

    /**
     * 获取java字段和数据库字段映射集合
     * @param entityClass 实体class对象
     * @param withId 有id
     * @return 映射集合
     * @throws Exception
     */
    private static List<PropertiesMapping> getPropertiesMappingList(Class entityClass, boolean withId) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFields(fields);
        List<PropertiesMapping> propertiesMappings = new ArrayList<>();
        for (Field field : fields) {
            if (!withId && field.getName().equals("id")) continue;
            PropertiesMapping mapping = new PropertiesMapping();
            mapping.setCol(BaseDatabaseHelper.getDatabaseFieldName(field.getName()));
            mapping.setProp(field.getName());
            propertiesMappings.add(mapping);
        }

        return propertiesMappings;
    }

    /**
     * 获取数据库字段组合
     * @param entityClass 实体class对象
     * @return 字段组合string
     * @throws Exception
     */
    private static String generateEntityCols(Class entityClass) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFields(fields);

        String cols = "";
        int index = 0;
        for (Field field : fields) {
            if (index++ > 0) cols += ", ";
            cols += BaseDatabaseHelper.getDatabaseFieldName(field.getName());
        }

        return cols;
    }

    /**
     * 生成插入列字符串
     * @param entityClass 实体class对象
     * @return 字段组合string
     * @throws Exception
     */
    private static String generateInsertCols(Class entityClass) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFieldsAndId(fields);

        String cols = "";
        int index = 0;
        for (Field field : fields) {
            if (index++ > 0) cols += ", ";
            cols += BaseDatabaseHelper.getDatabaseFieldName(field.getName());
        }

        return cols;
    }

    /**
     * 生成插入值字符串
     * @param entityClass 实体class
     * @return 插入值
     * @throws Exception
     */
    private static String generateInsertValues(Class entityClass) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFieldsAndId(fields);

        String values = "";
        int index = 0;
        for (Field field : fields) {
            if (index++ > 0) values += ", ";
            values += "#{" + field.getName() + "}";
        }

        return values;
    }

    /**
     * 生成批量插入字符串
     * @param entityClass 实体class
     * @return 插入值
     * @throws Exception
     */
    private static String generateBatchInsertValues(Class entityClass) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFieldsAndId(fields);

        String values = "";
        int index = 0;
        for (Field field : fields) {
            if (index++ > 0) values += ", ";
            values += "#{item." + field.getName() + "}";
        }

        return values;
    }

    /**
     * 排除非列属性
     * @param fields 所有属性
     * @throws Exception
     */
    private static void excludeNotColFields(List<Field> fields) throws Exception {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            if (field.getAnnotations().length == 0) {
                fields.remove(field);
            }
        }
    }

    /**
     * 排除非列和id属性
     * @param fields 所有属性
     * @throws Exception
     */
    private static void excludeNotColFieldsAndId(List<Field> fields) throws Exception {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            if (field.getAnnotations().length == 0) {
                fields.remove(field);
            }
            if (field.getName().equals("id")) {
                fields.remove(field);
            }
        }
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
