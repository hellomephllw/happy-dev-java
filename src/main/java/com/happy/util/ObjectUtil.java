package com.happy.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @description: 对象工具类
 * @author: llw
 * @date: 2018-11-15
 */
public class ObjectUtil {

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(ObjectUtil.class);

    /**
     * 获取XStream
     * @param withCData 是否包含cdata
     * @return xstream
     */
    public static XStream getXStream(boolean withCData) {
        return new XStream(new XppDriver(new NoNameCoder()) {

            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    // 对所有xml节点的转换都增加CDATA标记
                    boolean cdata = withCData;

                    @Override
                    @SuppressWarnings("rawtypes")
                    public void startNode(String name, Class clazz) {
                        super.startNode(name, clazz);
                    }

                    //去除双下划线
                    @Override
                    public String encodeNode(String name) {
                        return name;
                    }

                    @Override
                    protected void writeText(QuickWriter writer, String text) {
                        if (cdata) {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }
                    }
                };
            }
        });
    }

    /**
     * 把对象转换为xml字符串
     * @param object 对象
     * @return xml
     * @throws Exception
     */
    public static String transferObjectToXml(Object object) {
        XStream xstream = getXStream(false);
        xstream.processAnnotations(object.getClass());

        return xstream.toXML(object);
    }

    /**
     * 把对象转换为xml字符串，属性值加入cdata
     * @param object 对象
     * @return xml
     * @throws Exception
     */
    public static String transferObjectToXmlWithCData(Object object) {
        XStream xstream = getXStream(true);
        xstream.processAnnotations(object.getClass());

        return xstream.toXML(object);
    }

    /**
     * 把xml字符串转换为对象
     * @param xml   xml
     * @param clazz 对象所属class
     * @return 对象
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T transferXmlToObject(String xml, Class<T> clazz) {
        XStream xstream = getXStream(false);
        xstream.processAnnotations(clazz);

        return (T) xstream.fromXML(xml);
    }

    /**
     * 把对象的所有属性转到另一个对象
     * @param object             被转移对象
     * @param targetObjectClazz  目标对象class
     * @return 目标对象
     * @throws Exception
     */
    public static <T> T transferObjectValToAnother(Object object, Class<T> targetObjectClazz) {
        T targetObject = null;
        try {
            //确定另一个对象
            targetObject = targetObjectClazz.newInstance();

            //递归迭代转移所有属性
            transferRecursively(object, object.getClass(), targetObject);
        } catch (Exception e) {
            logger.error("转移对象属性发生错误", e);
        }

        return targetObject;
    }

    /**
     * 递归迭代转移所有属性
     * @param object       对象
     * @param objectClazz  对象类模板
     * @param targetObject 目标对象
     * @return 目标对象
     * @throws Exception
     */
    private static Object transferRecursively(Object object, Class objectClazz, Object targetObject) throws Exception {
        //转移当前object或object基类的类模板的所有属性
        transfer(object, objectClazz, targetObject, targetObject.getClass());

        //如果被转移对象还有基类，则继续迭代
        if (hasParent(objectClazz)) {
            return transferRecursively(object, objectClazz.getSuperclass(), targetObject);
        }

        return targetObject;
    }

    /**
     * 转移属性
     * @param object            被转移对象
     * @param objectClazz       被转移对象class
     * @param targetObject      目标对象
     * @param targetObjectClazz 目标对象class
     * @throws Exception
     */
    private static void transfer(Object object, Class objectClazz, Object targetObject, Class targetObjectClazz) throws Exception {
        for (Field voField : targetObjectClazz.getDeclaredFields()) {
            //自身属性
            fillVal(voField, object, objectClazz, targetObject, targetObjectClazz);
        }

        //如果目标对象有基类，则继续迭代
        if (hasParent(targetObjectClazz)) {
            transfer(object, objectClazz, targetObject, targetObjectClazz.getSuperclass());
        }
    }

    /**
     * 转移某属性
     * @param currField     某属性
     * @param object        被转移对象
     * @param objectClazz   被转移对象class
     * @param targetObject  目标对象
     * @param anotherClazz  目标对象class
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private static <T> void fillVal(Field currField, Object object, Class objectClazz, T targetObject, Class<T> anotherClazz) throws Exception {
        for (Field objField : objectClazz.getDeclaredFields()) {
            if (currField.getName().equals(objField.getName()) && currField.getType() == objField.getType() && !currField.getName().equals("serialVersionUID")) {
                Method method;
                String transformName = objField.getName().substring(0, 1).toUpperCase() + objField.getName().substring(1);
                if (objField.getGenericType().toString().toLowerCase().equals("boolean")) {
                    if (transformName.substring(0, 2).toLowerCase().equals("is")) {
                        transformName = objField.getName().substring(2, 3).toUpperCase() + objField.getName().substring(3);
                        method = objectClazz.getMethod(objField.getName());
                    } else {
                        method = objectClazz.getMethod("is" + transformName);
                    }
                } else {
                    method = objectClazz.getMethod("get" + transformName);
                }
                anotherClazz.getMethod("set" + transformName, objField.getType()).invoke(targetObject, method.invoke(object));
                break;
            }
        }
    }

    /**
     * 判断是否还有继承的基类
     * @param clazz 被判定对象
     * @return 判定结果
     * @throws Exception
     */
    private static boolean hasParent(Class clazz) throws Exception {
        return clazz.getSuperclass() != Object.class;
    }

}
