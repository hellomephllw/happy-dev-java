package com.happy.express.code;

import com.happy.express.persist.annotation.HappyCol;
import com.happy.express.persist.mysql.BaseGenerator;
import com.happy.express.persist.mysql.helper.BaseDatabaseHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 代码生成器帮助工具
 * @author: liliwen
 * @date: 2021-07-08
 */
public class CodeGeneratorHelper {

    /**
     * 首字母大写
     * @param letter 字母
     * @return 首字母大写结果
     */
    public static String makeFirstLetterUpper(String letter) {
        return letter.substring(0, 1).toUpperCase() + letter.substring(1);
    }

    /**
     * 首字母小写
     * @param letter 字母
     * @return 首字母小写结果
     */
    public static String makeFirstLetterLower(String letter) {
        return letter.substring(0, 1).toLowerCase() + letter.substring(1);
    }

    /**
     * 获取子模块名
     * @param classPackagePath 包路径
     * @return 子模块
     */
    public static String getModuleName(String classPackagePath) {
        String[] fragments = classPackagePath.split("entity")[1].split("\\.");
        if (fragments.length == 3) {
            //子模块
            return fragments[1];
        }

        return null;
    }

    /**
     * 获取java字段和数据库字段映射集合
     * @param entityClass 实体class对象
     * @param withId 有id
     * @return 映射集合
     * @throws Exception
     */
    public static List<PropertiesMapping> getPropertiesMappingList(Class entityClass, boolean withId) throws Exception {
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
    public static String generateEntityCols(Class entityClass) throws Exception {
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
    public static String generateInsertCols(Class entityClass) throws Exception {
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
     * @param withId 带id
     * @return 插入值
     * @throws Exception
     */
    public static String generateInsertValues(Class entityClass, boolean withId) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFieldsAndId(fields);

        String values = "";
        int index = 0;
        for (Field field : fields) {
            if (!withId && field.getName().equals("id")) continue;
            if (index++ > 0) values += ", ";
            values += "#{" + field.getName() + "}";
        }

        return values;
    }

    /**
     * 生成批量插入字符串
     * @param entityClass 实体class
     * @param withId 带id
     * @return 插入值
     * @throws Exception
     */
    public static String generateBatchInsertValues(Class entityClass, boolean withId) throws Exception {
        List<Field> fields = BaseGenerator.collectAllFields(entityClass);
        excludeNotColFieldsAndId(fields);

        String values = "";
        int index = 0;
        for (Field field : fields) {
            if (!withId && field.getName().equals("id")) continue;
            if (index++ > 0) values += ", ";
            values += "#{item." + field.getName() + "}";
        }

        return values;
    }

    /**
     * 排除非列属性
     * @param fields 所有属性
     */
    private static void excludeNotColFields(List<Field> fields) {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            if (field.getAnnotations().length == 0 && field.getAnnotation(HappyCol.class) == null) {
                fields.remove(field);
            }
        }
    }

    /**
     * 排除非列和id属性
     * @param fields 所有属性
     */
    private static void excludeNotColFieldsAndId(List<Field> fields) {
        for (int i = 0; i < fields.size(); ++i) {
            Field field = fields.get(i);
            if (field.getAnnotations().length == 0 && field.getAnnotation(HappyCol.class) == null) {
                fields.remove(field);
            }
            if (field.getName().equals("id")) {
                fields.remove(field);
            }
        }
    }

}
