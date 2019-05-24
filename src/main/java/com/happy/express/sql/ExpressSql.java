package com.happy.express.sql;

import com.happy.util.ReflectionUtil;
import com.happy.util.RegexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 引入迅捷Sql
 * @author: llw
 * @date: 2019-05-09
 */
public class ExpressSql {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(ExpressSql.class);

    /**所有实体, 键为包路径, 值为实体类模板*/
    public static Map<String, Class> entities = new HashMap<>();
    /**实体包路径*/
    private static String entitiesPackagePath = null;

    /**
     * 开启
     * @param entitiesPackagePath 实体包路径
     * @param openObverse 正向sql(对象to sql)
     * @param openReverse 逆向sql(直接sql)
     */
    public static ExpressSql startup(String entitiesPackagePath, boolean openObverse, boolean openReverse) {
        try {
            //初始化实体包路径
            ExpressSql.entitiesPackagePath = entitiesPackagePath;
            //读取所有实体
            entitiesReader();
        } catch (Exception e) {
            logger.error("启动ExpressSql发生错误", e);
        }

        return new ExpressSql();
    }

    /**
     * 获取表名称
     * @param className 类名(非全路径)
     * @return 表名称
     */
    public static String getTableName(String className) {
        String tableName = null;
        try {
            int count = 0;
            for (String key : entities.keySet()) {
                if (RegexUtil.test(".*" + className + "$", key)) {
                    ++count;
                    Class entityClass = entities.get(key);
                    tableName = ReflectionUtil.getTableName(entityClass);
                }
            }
            if (count > 1) throw new Exception("项目中存在相同名称的实体");
        } catch (Exception e) {
            logger.error("获取实体表名出错", e);
        }

        return tableName;
    }

    /**
     * 获取实体
     * @return 实体map集合
     */
    public static Map<String, Class> getEntities() {
        return entities;
    }

    /**
     * 读取所有实体
     */
    private static void entitiesReader() throws Exception {
        File packageDir = new File(getRootClassPath() + transferDotToSlash(entitiesPackagePath));
        for (File file : packageDir.listFiles()) {
            if (file.isFile()) {
                //基本模块
                collectClasses(file, null);
            } else {
                //子模块
                String dirName = file.getName();
                for (File innerFile : file.listFiles()) {
                    collectClasses(innerFile, dirName);
                }
            }
        }
    }

    /**
     * 获取类加载路径根目录
     * @return 类加载路径根目录
     */
    private static String getRootClassPath() {
        return ExpressSql.class
                .getResource("/")
                .getPath()
                .replaceAll("out/test/classes", "out/production/classes");
    }

    /**
     * 把点变为斜杠
     * @param path 路径
     * @return 转换后的路径
     */
    private static String transferDotToSlash(String path) {
        return path.replaceAll("\\.", "/");
    }

    /**
     * 收集类模板
     * @param file 文件
     * @param childModuleName 子模块
     * @throws Exception
     */
    private static void collectClasses(File file, String childModuleName) throws Exception {
        if (file.isFile() && file.getName().endsWith(".class")) {
            String fileName = file.getName().split("\\.")[0];
            String classPath = entitiesPackagePath + "." + (childModuleName != null ? childModuleName + "." : "") + fileName;
            Class entityClass = ExpressSql.class.getClassLoader().loadClass(classPath);
            entities.put(classPath, entityClass);
        }
    }

}
