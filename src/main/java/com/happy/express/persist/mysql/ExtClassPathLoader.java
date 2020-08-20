package com.happy.express.persist.mysql;

import com.happy.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @description: 加载jar包工具
 * @author: llw
 * @date: 2018-11-23
 */
public final class ExtClassPathLoader {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(ExtClassPathLoader.class);

    /**添加加载类工具*/
    private static Method addURL = initAddMethod();
    /**类加载器*/
    private static URLClassLoader classloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
    /**所有jar*/
    private static List<File> jars = new ArrayList<>();

    /**
     * 加载该目录下所有jar包
     * @param dirPath 目录路径
     * @throws Exception
     */
    public static void loadAllJars(String dirPath) throws Exception {
        File file = new File(dirPath);
        loopFiles(file);
    }

    /**
     * 加载该目录下所有class文件
     * @param dirPath 目录路径
     * @throws Exception
     */
    private static void loadAllClasses(String dirPath) throws Exception {
        File file = new File(dirPath);
        loopDirs(file);
        loadAllClasses();
    }

    /**
     * 循环遍历目录，找出所有的资源路径。
     * @param file 当前遍历文件
     */
    private static void loopDirs(File file) throws Exception {
        // 资源文件只加载路径
        if (file.isDirectory()) {
            addURL(file);
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopDirs(tmp);
            }
        }
    }

    /**
     * 循环遍历目录，找出所有的jar包。
     * @param file 当前遍历文件
     */
    private static void loopFiles(File file) throws Exception {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            for (File tmp : tmps) {
                loopFiles(tmp);
            }
        } else {
            if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
                addURL(file);
                jars.add(file);
                logger.info("添加jar包: " + file);
            }
        }
    }

    /**
     * 加载所有jar包的class
     * @throws Exception
     */
    private static void loadAllClasses() throws Exception {
        for (File jar : jars) {
            loadClass(jar.getAbsolutePath());
        }
    }

    /**
     * 加载某个jar包下所有class文件
     * @param jarPath jar包路径
     * @throws Exception
     */
    private static void loadClass(String jarPath) throws Exception {
        JarFile jarFile = new JarFile(jarPath);
        Enumeration<JarEntry> en = jarFile.entries();
        while (en.hasMoreElements()) {
            JarEntry je = en.nextElement();
            String name = je.getName();
            String s5 = name.replace('/', '.');
            if (s5.endsWith(".class")) {
                String className = je.getName().substring(0, je.getName().length() - ".class".length()).replace('/', '.');
                classloader.loadClass(className);
                logger.info("已加载类: " + className);
            }
        }
    }

    /**
     * 初始化addUrl方法
     * @return 可访问addUrl方法的Method对象
     */
    private static Method initAddMethod() {
        try {
            Method add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            add.setAccessible(true);
            return add;
        } catch (Exception e) {
            LoggerUtil.printStackTrace(logger, e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过filepath加载文件到classpath
     * @param file 文件路径
     * @throws Exception 异常
     */
    private static void addURL(File file) throws Exception {
        addURL.invoke(classloader, new Object[]{file.toURI().toURL()});
    }

}
