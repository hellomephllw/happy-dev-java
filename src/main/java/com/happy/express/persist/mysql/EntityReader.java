package com.happy.express.persist.mysql;

import com.happy.express.code.BasicCodeGenerator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @description: 读取所有实体
 * @author: happy
 * @date: 2018-11-22
 */
public class EntityReader {

    /**类加载器*/
    private static ClassLoader classLoader;

    /**所有实体*/
    private static final List<Class> entities = new ArrayList<>();

    /**
     * 读取所有实体class
     * @param basePackagePath 基本包路径
     * @throws Exception
     */
    public static void readAllEntities(String basePackagePath) throws Exception {
        File packageDir = new File(basePackagePath + "/entity");
        for (File file : packageDir.listFiles()) {
            if (file.isFile()) {
                //基本模块
                addEntity(file, null);
            } else {
                //子模块
                String dirName = file.getName();
                for (File innerFile : file.listFiles()) {
                    addEntity(innerFile, dirName);
                }
            }
        }
    }

    /**
     * 获取所有实体
     * @return 所有实体
     */
    public static List<Class> getEntities() {
        return entities;
    }

    /**
     * 添加实体
     * @param entityFile 实体文件
     * @param partOfDir 部分包路径(可能有子模块)
     * @throws Exception
     */
    private static void addEntity(File entityFile, String partOfDir) throws Exception {
        if (entityFile.isFile() && entityFile.getName().endsWith(".java")) {
            String fileName = entityFile.getName();
            String className = TableGenerator.getUserConfigBasePackageFilePath().replaceAll("/", ".") + ".entity." + (partOfDir != null ? partOfDir + "." : "") + fileName.split("\\.")[0];
            Class entityClass = getClassPathClassLoader().loadClass(className);
            entities.add(entityClass);
        }
    }

    /**
     * 获取所有class文件的url路径
     * @return 所有class文件的url路径
     * @throws Exception
     */
    private static URL getClassPathUrl() throws Exception {
        return new URL("file://" + BasicCodeGenerator.getBuildClassPath() + "/");
    }

    /**
     * 获取加载所有class文件的类加载器
     * @return 类加载器
     * @throws Exception
     */
    private static ClassLoader getClassPathClassLoader() throws Exception {
        if (classLoader == null) {
            classLoader = new URLClassLoader(new URL[]{getClassPathUrl()});
        }
        return classLoader;
    }

}
