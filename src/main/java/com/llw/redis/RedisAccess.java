package com.llw.redis;

import com.llw.util.RegexUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
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
     * 设置过期时间
     * @param completedKey 完整的key(即末尾追加类型)
     * @param duration 过期时间(单位: 秒)
     */
    private void expire(String completedKey, long duration) {
        redisTemplate.expire(completedKey, duration, TimeUnit.SECONDS);
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
    public void remove(String key) {
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
     * 补充string的key
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

}
