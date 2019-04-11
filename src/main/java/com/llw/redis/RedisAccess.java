package com.llw.redis;

import ch.qos.logback.classic.pattern.DateConverter;
import com.llw.util.RegexUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @description: redis快速访问
 * @author: llw
 * @date: 2018-11-27
 */
@Component
public class RedisAccess {

    @Autowired
    private RedisTemplate redisTemplate;
    /**默认时间*/
    private long defaultDuration = 60 * 60 * 24;

    /**
     * 获取redistemplate
     * @return redistemplate
     */
    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    /**
     * 设置过期时间
     * @param completedKey 完整的key(即末尾追加类型)
     * @param duration 过期时间(单位: 秒)
     */
    private void expire(String completedKey, long duration) {
        redisTemplate.expire(completedKey, duration, TimeUnit.SECONDS);
    }

    /**
     * 重置时间格式
     */
    private void dateFormater() {
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class clazz, Object value) {
                if (value == null) {
                    return null;
                }
                if (value instanceof String) {
                    String str = (String) value;
                    if (str.trim().equals("")) {
                        return null;
                    }
                    List<SimpleDateFormat> formats = new LinkedList<>();
                    formats.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US));
                    formats.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                    formats.add(new SimpleDateFormat("yyyy-MM-dd"));
                    for (SimpleDateFormat simpleDateFormat : formats) {
                        try {
                            return simpleDateFormat.parse(str);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                return null;
            }
        }, Date.class);
    }

    //================================string
    /**
     * 补充string的key
     * @param key 键
     * @return 完整🉐️的key(即末尾追加类型)
     */
    private String keyString(String key) {
        return RegexUtil.find("\\.string$", key) ? key : key + ".string";
    }

    /**
     * 存入string
     * @param key 键
     * @param value 值
     */
    public void putString(String key, String value) {
        redisTemplate.opsForValue().set(keyString(key), value);
        expire(keyString(key), defaultDuration);
    }

    /**
     * 存入string
     * @param key 键
     * @param value 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void putString(String key, String value, long durationSecond) {
        redisTemplate.opsForValue().set(keyString(key), value);
        expire(keyString(key), durationSecond);
    }

    /**
     * 获取string
     * @param key 键
     * @return 值
     */
    public String getString(String key) {
        return (String) redisTemplate.opsForValue().get(keyString(key));
    }

    /**
     * 根据key删除
     * @param key 键
     */
    public void removeString(String key) {
        redisTemplate.delete(keyString(key));
    }

    /**
     * 是否存在string
     * @param key 键
     * @return 是否存在
     */
    public boolean existString(String key) {
        return getString(key) != null;
    }

    /**
     * 设置string的过期时间
     * @param key 键
     * @param durationSecond 时长(单位: 秒)
     */
    public void expireString(String key, long durationSecond) {
        redisTemplate.expire(keyString(key), durationSecond, TimeUnit.SECONDS);
    }

    //================================hash
    /**
     * 补充对象的key
     * @param key 键
     * @return 完整🉐️的key(即末尾追加类型)
     */
    private String keyObject(String key) {
        return RegexUtil.find("\\.hash$", key) ? key : key + ".hash";
    }

    /**
     * 存入对象
     * @param key 键
     * @param object 对象
     * @param <T> 范型
     * @throws Exception
     */
    public <T> void putObject(String key, T object) throws Exception {
        redisTemplate.opsForHash().putAll(keyObject(key), BeanUtils.describe(object));
        expire(keyObject(key), defaultDuration);
    }

    /**
     * 存入对象
     * @param key 键
     * @param object 对象
     * @param durationSecond 时长(单位: 秒)
     * @param <T> 范型
     * @throws Exception
     */
    public <T> void putObject(String key, T object, long durationSecond) throws Exception {
        redisTemplate.opsForHash().putAll(keyObject(key), BeanUtils.describe(object));
        expire(keyObject(key), durationSecond);
    }

    /**
     * 存入对象(map)
     * @param key 键
     * @param objectMap 对象(map)
     * @throws Exception
     */
    public void putObject(String key, Map<String, Object> objectMap) throws Exception {
        redisTemplate.opsForHash().putAll(keyObject(key), objectMap);
        expire(keyObject(key), defaultDuration);
    }

    /**
     * 存入对象(map)
     * @param key 键
     * @param objectMap 对象(map)
     * @param durationSecond 时长(单位: 秒)
     * @throws Exception
     */
    public void putObject(String key, Map<String, Object> objectMap, long durationSecond) throws Exception {
        redisTemplate.opsForHash().putAll(keyObject(key), objectMap);
        expire(keyObject(key), durationSecond);
    }

    /**
     * 获取对象(map)
     * @param key 键
     * @param clazz 对象(map)
     * @param <T> 范型
     * @return 对象
     * @throws Exception
     */
    public <T> T getObject(String key, Class<T> clazz) throws Exception {
        T bean = clazz.newInstance();
        dateFormater();
        BeanUtils.populate(bean, redisTemplate.opsForHash().entries(keyObject(key)));

        return bean;
    }

    /**
     * 删除对象
     * @param key 键
     */
    public void removeObject(String key) {
        redisTemplate.delete(keyObject(key));
    }

    /**
     * 是否存在对象
     * @param key 键
     * @return 是否存在
     */
    public boolean existObject(String key) {
        return redisTemplate.opsForHash().entries(keyObject(key)).keySet().size() != 0;
    }

    /**
     * 设置对象属性
     * @param key 键
     * @param fieldName 属性名
     * @param value 值
     */
    public void putObjectField(String key, String fieldName, Object value) {
        redisTemplate.opsForHash().put(keyObject(key), fieldName, value);
        expire(keyObject(key), defaultDuration);
    }

    /**
     * 设置对象属性
     * @param key 键
     * @param fieldName 属性名
     * @param value 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void putObjectField(String key, String fieldName, Object value, long durationSecond) {
        redisTemplate.opsForHash().put(keyObject(key), fieldName, value);
        expire(keyObject(key), durationSecond);
    }

    /**
     * 获取对象属性值
     * @param key 键
     * @param fieldName 属性名
     * @return 值
     */
    public Object getObjectField(String key, String fieldName) {

        return redisTemplate.opsForHash().get(keyObject(key), fieldName);
    }

    /**
     * 删除对象属性
     * @param key 键
     * @param fieldName 属性名
     * @return 值
     */
    public void removeObjectField(String key, String fieldName) {
        redisTemplate.opsForHash().delete(keyObject(key), fieldName);
    }

    /**
     * 是否存在对象属性
     * @param key 键
     * @param fieldName 属性名
     * @return 是否存在
     */
    public boolean existObjectField(String key, String fieldName) {
        return redisTemplate.opsForHash().get(keyObject(key), fieldName) != null;
    }

    /**
     * 设置object的过期时间
     * @param key 键
     * @param durationSecond 时长(单位: 秒)
     */
    public void expireObject(String key, long durationSecond) {
        redisTemplate.expire(keyObject(key), durationSecond, TimeUnit.SECONDS);
    }

    //================================list
    /**
     * 补充list的key
     * @param key 键
     * @return 完整🉐️的key(即末尾追加类型)
     */
    private String keyList(String key) {
        return RegexUtil.find("\\.list$", key) ? key : key + ".list";
    }

    /**
     * 存入集合(尾部追加)
     * @param key 键
     * @param list 集合
     */
    public void putList(String key, List<?> list) {
        removeList(key);
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 存入集合(尾部追加)
     * @param key 键
     * @param list 集合
     * @param durationSecond 过期时间
     */
    public void putList(String key, List<?> list, long durationSecond) {
        removeList(key);
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), durationSecond);
    }

    /**
     * 获取集合
     * @param key 键
     * @return 集合
     */
    public List<?> getList(String key) {
        return redisTemplate.opsForList().range(keyList(key), 0, getListLength(key) - 1);
    }

    /**
     * 获取list长度
     * @param key 键
     * @return 长度
     */
    public long getListLength(String key) {
        return redisTemplate.opsForList().size(keyList(key));
    }

    /**
     * 根据索引获取集合元素
     * @param key 键
     * @param index 索引
     * @return
     */
    public Object getListItemByIndex(String key, int index) {
        return redisTemplate.opsForList().index(keyList(key), index);
    }



    /**
     * 删除list集合
     * @param key 键
     */
    public void removeList(String key) {
        redisTemplate.delete(keyList(key));
    }

    //================================set

    //================================sort set

}
