package com.llw.express.code;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 读取实体的属性
 * @author: llw
 * @date: 2018-11-21
 */
public class PackageReader {

    /**类加载器*/
    private static ClassLoader classLoader;

    /**所有实体, 键为包路径, 值为实体类模板*/
    public static Map<String, Class> entities = new HashMap<>();
    /**所有dao接口, 键为包路径, 值为dao接口模板*/
    public static Map<String, Class> daos = new HashMap<>();
    /**所有dao的实现类, 键为包路径, 值为dao实现类模板*/
    public static Map<String, Class> daoImpls = new HashMap<>();
    /**所有service接口, 键为包路径, 值为service接口模板*/
    public static Map<String, Class> services = new HashMap<>();
    /**所有service的实现类, 键为包路径, 值为service实现类模板*/
    public static Map<String, Class> serviceImpls = new HashMap<>();

    /**
     * 读取所有的实体
     * @param basePackagePath 基础的包路径
     * @throws Exception
     */
    public static void readAllEntities(String basePackagePath) throws Exception {
        File packageDir = new File(basePackagePath + "/entity");
        for (File file : packageDir.listFiles()) {
            if (file.isFile()) {
                //基本模块
                collectClasses(file, null, entities, "entity");
            } else {
                //子模块
                String dirName = file.getName();
                for (File innerFile : file.listFiles()) {
                    collectClasses(innerFile, dirName + ".", entities, "entity");
                }
            }
        }
    }

    /**
     * 读取所有的dao
     * @param basePackagePath 基础的包路径
     * @throws Exception
     */
    public static void readAllDaos(String basePackagePath) throws Exception {
        File packageDir = new File(basePackagePath + "/dao");
        for (File file : packageDir.listFiles()) {
            if (file.isFile()) {
                //基本模块dao接口
                collectClasses(file, null, daos, "dao");
            } else {
                if (file.getName().equals("impl")) {
                    String implDirName = file.getName();
                    for (File daoImplFile : file.listFiles()) {
                        //基本模块dao实现类
                        collectClasses(daoImplFile, implDirName + ".", daoImpls, "dao");
                    }
                } else {
                    //子模块
                    String childDirName = file.getName();
                    for (File childFile : file.listFiles()) {
                        if (childFile.isFile()) {
                            //子模块dao接口
                            collectClasses(childFile, childDirName + ".", daos, "dao");
                        } else {
                            //子模块dao实现类
                            String childImplDirName = childFile.getName();
                            for (File childImplFile : childFile.listFiles()) {
                                collectClasses(childImplFile, childDirName + "." + childImplDirName + ".", daoImpls, "dao");
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 读取所有的service
     * @param basePackagePath 基础的包路径
     * @throws Exception
     */
    public static void readAllServices(String basePackagePath) throws Exception {
        File packageDir = new File(basePackagePath + "/service");
        for (File file : packageDir.listFiles()) {
            if (file.isFile()) {
                //基本模块service接口
                collectClasses(file, null, services, "service");
            } else {
                if (file.getName().equals("impl")) {
                    String implDirName = file.getName();
                    for (File serviceImplFile : file.listFiles()) {
                        //基本模块service实现类
                        collectClasses(serviceImplFile, implDirName + ".", serviceImpls, "service");
                    }
                } else {
                    //子模块
                    String childDirName = file.getName();
                    for (File childFile : file.listFiles()) {
                        if (childFile.isFile()) {
                            //子模块service接口
                            collectClasses(childFile, childDirName + ".", services, "service");
                        } else {
                            //子模块service实现类
                            String childImplDirName = childFile.getName();
                            for (File childImplFile : childFile.listFiles()) {
                                collectClasses(childImplFile, childDirName + "." + childImplDirName + ".", serviceImpls, "service");
                            }
                        }
                    }
                }
            }
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

    /**
     * 创建类名(类的全路径, 包括包名)
     * @param partOfPath 文件名称(可能会有部分包名)
     * @return 类名
     * @throws Exception
     */
    private static String createClassName(String partOfPath) throws Exception {
        return BasicCodeGenerator.getUserConfigBasePackagePath().replaceAll("/", ".") + partOfPath;
    }

    /**
     * 收集类模版
     * @param file 类文件
     * @param partOfDir 部分目录路径
     * @param collector 收集器
     * @param type 类模版类型(只有entity、dao、service三种类型)
     * @throws Exception
     */
    private static void collectClasses(File file, String partOfDir, Map<String, Class> collector, String type) throws Exception {
        if (file.isFile() && file.getName().endsWith(".java")) {
            String fileName = file.getName();
            String className = createClassName("." + type + "." + (partOfDir != null ? partOfDir : "") + fileName.split("\\.")[0]);
            Class entityClass = getClassPathClassLoader().loadClass(className);
            collector.put(className, entityClass);
        }
    }

}
