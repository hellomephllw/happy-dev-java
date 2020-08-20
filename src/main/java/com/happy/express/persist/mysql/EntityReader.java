package com.happy.express.persist.mysql;

import com.happy.express.code.BasicCodeGenerator;
import com.happy.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @description: 读取所有实体
 * @author: llw
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
     * 在jar包的自身classPath下读取所有实体class
     * @param userConfigBasePackagePath 用户配置基础包路径
     * @throws Exception
     */
    public static void readAllEntitiesInJarClassPath(String userConfigBasePackagePath) throws Exception {
        String entityPackagePath = userConfigBasePackagePath + "/entity";
        String jarPath = FileUtil.getWebRootAbsolutePath().split("file:")[1].split("!")[0];
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (entry.isDirectory() || !name.contains(entityPackagePath) || !name.endsWith(".class")) continue;
            name = name.split("classes")[1].replace('/', '.');
            String className = name.substring(1, name.length() - ".class".length());
            Class entityClass = EntityReader.class.getClassLoader().loadClass(className);
            entities.add(entityClass);
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
