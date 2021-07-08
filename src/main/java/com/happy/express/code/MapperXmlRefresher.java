package com.happy.express.code;

import com.happy.express.persist.mysql.HappyTableGenerator;
import com.happy.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @description: mybatis mapper xml更新器
 * @author: liliwen
 * @date: 2021-07-07
 */
@Slf4j
public class MapperXmlRefresher extends BasePathResolver {

    private static final String TAB_SPACE = "    ";

    private static final String MAPPER_BASE_RESULT_MAP = "<resultMap id=\"baseResultMap\"";
    private static final String MAPPER_TABLE_NAME = "<sql id=\"tableName\"";
    private static final String MAPPER_BASE_COLS = "<sql id=\"baseColumns\"";
    private static final String MAPPER_BASE_COLS_WITHOUT_ID = "<sql id=\"baseColumnsWithoutId\"";
    private static final String MAPPER_ADD = "<insert id=\"add\"";
    private static final String MAPPER_ADD_BATCH = "<insert id=\"addBatch\"";
    private static final String MAPPER_UPDATE = "<update id=\"update\"";
    private static final String MAPPER_UPDATE_BATCH = "<update id=\"updateBatch\"";

    /**
     * 所有更新mapper xml文件
     * @param userConfigRelativePackagePath 用户配置的包路径
     */
    public static void refreshMapperXmls(String userConfigRelativePackagePath) throws Exception {
        //初始化路径
        initPath(userConfigRelativePackagePath);
        //读取所有实体
        PackageReader.readAllEntities(getBasePackagePath());
        //更新mapper xml
        refresh();
    }

    private static void refresh() {
        Map<String, Class> entities = PackageReader.entities;
        for (String classPackagePath : entities.keySet()) {
            try {
                Class clazz = entities.get(classPackagePath);

                String moduleName = CodeGeneratorHelper.getModuleName(classPackagePath);
                String dirPath = getMybatisMapperPath() + (StringUtil.isEmpty(moduleName) ? "" : "/" + moduleName);
                String fileName = clazz.getSimpleName() + "Mapper.xml";

                //mapper文件不存在则跳过
                File mapperFile = new File(dirPath + "/" + fileName);
                if (!mapperFile.exists()) continue;

                //读取
                StringBuilder mapperContent = new StringBuilder(readMapperContent(mapperFile));
                //修改
                modify(clazz, mapperContent);
                //写出
                writeOut(mapperFile, mapperContent.toString());
            } catch (Exception e) {
                log.error("更新entity「" + entities.get(classPackagePath).getName()+ "」的mapper文件出错: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 读取mapper文件内容
     * @param mapperFile mapper文件
     * @return 内容
     */
    private static String readMapperContent(File mapperFile) {
        StringBuilder finalStr = new StringBuilder();
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader buffReader = null;
        try {
            fileInputStream = new FileInputStream(mapperFile);
            inputStreamReader = new InputStreamReader(fileInputStream);
            buffReader = new BufferedReader(inputStreamReader);
            finalStr = new StringBuilder();
            String tmpStr = "";
            int count = 0;
            while ((tmpStr = buffReader.readLine()) != null) {
                if (count++ > 0) finalStr.append("\n");
                finalStr.append(tmpStr);
            }
        } catch (IOException e) {
            log.error("读取mapper文件内容出错: " + e.getMessage(), e);
        } finally {
            try {
                if (buffReader != null) buffReader.close();
                if (inputStreamReader != null) inputStreamReader.close();
                if (fileInputStream != null) fileInputStream.close();
            } catch (IOException e) {
                log.error("关闭流出错: " + e.getMessage(), e);
            }
        }

        return finalStr.toString();
    }

    /**
     * 更新mapper
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modify(Class clazz, StringBuilder mapperContent) throws Exception {
        modifyBaseResultMap(clazz, mapperContent);
        modifyTableName(clazz, mapperContent);
        modifyBaseCol(clazz, mapperContent);
        modifyBaseColWithoutId(clazz, mapperContent);
        modifyAdd(clazz, mapperContent);
        modifyAddBatch(clazz, mapperContent);
        modifyUpdate(clazz, mapperContent);
        modifyUpdateBatch(clazz, mapperContent);
    }

    /**
     * 输出文件内容
     * @param mapperFile mapper文件
     * @param content 内容
     */
    private static void writeOut(File mapperFile, String content) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(mapperFile);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
        } catch (IOException e) {
            log.error("更新写入mapper文件出错: " + e.getMessage(), e);
        } finally {
            try {
                if (bufferedWriter != null) bufferedWriter.close();
                if (fileWriter != null) fileWriter.close();
            } catch (IOException e) {
                log.error("关闭流出错: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 更新BaseResultMap
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyBaseResultMap(Class clazz, StringBuilder mapperContent) throws Exception {
        String propertyTagPrefix = "<result ";
        String closeResultMapTag = "</resultMap>";
        int startIndex = mapperContent.indexOf(MAPPER_BASE_RESULT_MAP);
        int startReplaceIndex = mapperContent.indexOf(propertyTagPrefix, startIndex);
        int endReplaceIndex = mapperContent.indexOf(closeResultMapTag, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        List<PropertiesMapping> propertiesMappings = CodeGeneratorHelper.getPropertiesMappingList(clazz, false);
        int count = 0;
        for (PropertiesMapping propertiesMapping : propertiesMappings) {
            if (count++ > 0) replaceContent.append(TAB_SPACE).append(TAB_SPACE);
            replaceContent.append("<result column=\"");
            replaceContent.append(propertiesMapping.getCol());
            replaceContent.append("\" property=\"");
            replaceContent.append(propertiesMapping.getProp());
            replaceContent.append("\"/>");
            replaceContent.append(System.lineSeparator());
        }
        replaceContent.append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新表名
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyTableName(Class clazz, StringBuilder mapperContent) throws Exception {
        commonModifySqlTag(mapperContent, MAPPER_TABLE_NAME, HappyTableGenerator.getTableName(clazz));
    }

    /**
     * 更新baseCols
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyBaseCol(Class clazz, StringBuilder mapperContent) throws Exception {
        commonModifySqlTag(mapperContent, MAPPER_BASE_COLS, CodeGeneratorHelper.generateEntityCols(clazz));
    }

    /**
     * 更新insertCols
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyBaseColWithoutId(Class clazz, StringBuilder mapperContent) throws Exception {
        commonModifySqlTag(mapperContent, MAPPER_BASE_COLS_WITHOUT_ID, CodeGeneratorHelper.generateInsertCols(clazz));
    }

    /**
     * 更新add
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyAdd(Class clazz, StringBuilder mapperContent) throws Exception {
        String valuesKeyWord = "values ";
        String endInsertTag = "</insert>";
        int startIndex = mapperContent.indexOf(MAPPER_ADD);
        int startReplaceIndex = mapperContent.indexOf(valuesKeyWord, startIndex);
        int endReplaceIndex = mapperContent.indexOf(endInsertTag, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        replaceContent.append(valuesKeyWord).append("(");
        replaceContent.append(CodeGeneratorHelper.generateInsertValues(clazz, false));
        replaceContent.append(")").append(System.lineSeparator()).append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新批量add
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyAddBatch(Class clazz, StringBuilder mapperContent) throws Exception {
        String foreachOpenTagEnd = "separator=\",\">";
        String endForeachTag = "</foreach>";
        int startIndex = mapperContent.indexOf(MAPPER_ADD_BATCH);
        int startReplaceIndex = mapperContent.indexOf(foreachOpenTagEnd, startIndex);
        int endReplaceIndex = mapperContent.indexOf(endForeachTag, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        replaceContent.append(foreachOpenTagEnd);
        replaceContent.append(System.lineSeparator()).append(TAB_SPACE).append(TAB_SPACE).append(TAB_SPACE);
        replaceContent.append("(");
        replaceContent.append(CodeGeneratorHelper.generateBatchInsertValues(clazz, false));
        replaceContent.append(")");
        replaceContent.append(System.lineSeparator()).append(TAB_SPACE).append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新update
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyUpdate(Class clazz, StringBuilder mapperContent) throws Exception {
        String setKeyWord = "set";
        String whereKeyWord = "where";
        int startIndex = mapperContent.indexOf(MAPPER_UPDATE);
        int startReplaceIndex = mapperContent.indexOf(setKeyWord, startIndex);
        int endReplaceIndex = mapperContent.indexOf(whereKeyWord, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        replaceContent.append("set").append(System.lineSeparator());
        replaceContent.append(updatePropsSql(clazz, false));
        replaceContent.append(System.lineSeparator()).append(TAB_SPACE).append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新批量update
     * @param clazz 实体class
     * @param mapperContent mapper内容
     * @throws Exception
     */
    private static void modifyUpdateBatch(Class clazz, StringBuilder mapperContent) throws Exception {
        String setKeyWord = "set";
        String whereKeyWord = "where";
        int startIndex = mapperContent.indexOf(MAPPER_UPDATE_BATCH);
        int startReplaceIndex = mapperContent.indexOf(setKeyWord, startIndex);
        int endReplaceIndex = mapperContent.indexOf(whereKeyWord, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        replaceContent.append("set").append(System.lineSeparator());
        replaceContent.append(updatePropsSql(clazz, true));
        replaceContent.append(System.lineSeparator()).append(TAB_SPACE).append(TAB_SPACE).append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新sql标签通用方法
     * @param mapperContent mapper内容
     * @param targetMark 目标标记
     * @param replaceString 替换的内容
     */
    private static void commonModifySqlTag(StringBuilder mapperContent, String targetMark, String replaceString) {
        String tagEnd = ">";
        String closeSqlTag = "</sql>";
        int startIndex = mapperContent.indexOf(targetMark);
        int startReplaceIndex = mapperContent.indexOf(tagEnd, startIndex) + 1;
        int endReplaceIndex = mapperContent.indexOf(closeSqlTag, startIndex);

        StringBuilder replaceContent = new StringBuilder();
        replaceContent.append(System.lineSeparator());
        replaceContent.append(TAB_SPACE).append(TAB_SPACE);
        replaceContent.append(replaceString);
        replaceContent.append(System.lineSeparator()).append(TAB_SPACE);

        mapperContent.replace(startReplaceIndex, endReplaceIndex, replaceContent.toString());
    }

    /**
     * 更新update属性的sql
     * @param clazz 实体class
     * @param moreOneTab 多一个tab
     * @return 最终sql
     * @throws Exception
     */
    private static String updatePropsSql(Class clazz, boolean moreOneTab) throws Exception {
        StringBuilder resultString = new StringBuilder();
        List<PropertiesMapping> propertiesMappings = CodeGeneratorHelper.getPropertiesMappingList(clazz, false);
        int count = 0;
        int total = propertiesMappings.size();
        for (PropertiesMapping propertiesMapping : propertiesMappings) {
            resultString.append(TAB_SPACE).append(TAB_SPACE).append(TAB_SPACE);
            if (moreOneTab) resultString.append(TAB_SPACE);
            resultString.append(propertiesMapping.getCol()).append("=#{item.").append(propertiesMapping.getCol()).append("}");
            if (++count < total) resultString.append(",").append(System.lineSeparator());
        }

        return resultString.toString();
    }

    public static void main(String[] args) {
        try {
            //所有更新mapper xml文件
            refreshMapperXmls(args[0]);
        } catch (Exception e) {
            log.error("更新mapper xml文件出错: " + e.getMessage(), e);
        }
    }

}
