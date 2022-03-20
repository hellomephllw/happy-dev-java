package com.happy.redis;

import com.happy.util.LoggerUtil;
import com.happy.util.RegexUtil;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
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

    /**logger*/
    private static Logger logger = LoggerFactory.getLogger(RedisAccess.class);

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
     * 重置时间格式(使copyProperties支持时间转换)
     */
    private void dateFormatter() {
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
     * @return 完整的key(即末尾追加类型)
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
     * @return 完整的key(即末尾追加类型)
     */
    private String keyObject(String key) {
        return RegexUtil.find("\\.hash$", key) ? key : key + ".hash";
    }

    /**
     * 存入对象
     * @param key 键
     * @param object 对象
     * @param <T> 范型
     */
    public <T> void putObject(String key, T object) {
        redisTemplate.opsForHash().putAll(keyObject(key), new BeanMap(object));
        expire(keyObject(key), defaultDuration);
    }

    /**
     * 存入对象
     * @param key 键
     * @param object 对象
     * @param durationSecond 时长(单位: 秒)
     * @param <T> 范型
     */
    public <T> void putObject(String key, T object, long durationSecond) {
        redisTemplate.opsForHash().putAll(keyObject(key), new BeanMap(object));
        expire(keyObject(key), durationSecond);
    }

    /**
     * 存入对象(map)
     * @param key 键
     * @param objectMap 对象(map)
     */
    public void putObject(String key, Map<String, Object> objectMap) {
        redisTemplate.opsForHash().putAll(keyObject(key), objectMap);
        expire(keyObject(key), defaultDuration);
    }

    /**
     * 存入对象(map)
     * @param key 键
     * @param objectMap 对象(map)
     * @param durationSecond 时长(单位: 秒)
     */
    public void putObject(String key, Map<String, Object> objectMap, long durationSecond) {
        redisTemplate.opsForHash().putAll(keyObject(key), objectMap);
        expire(keyObject(key), durationSecond);
    }

    /**
     * 获取对象(map)
     * @param key 键
     * @param clazz 对象(map)
     * @param <T> 范型
     * @return 对象
     */
    public <T> T getObject(String key, Class<T> clazz) {
        if (existObject(key)) {
            T bean = null;
            try {
                bean = clazz.newInstance();
                dateFormatter();
                BeanUtils.populate(bean, redisTemplate.opsForHash().entries(keyObject(key)));
            } catch (Exception e) {
                logger.error("redis访问的对象反射出错: " + e.getMessage(), e);
                throw new RuntimeException("redis访问的对象反射出错: " + e.getMessage(), e);
            }

            return bean;
        }

        return null;
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
     * 获取对象属性(long类型属性)
     * @param key 健
     * @param fieldName 属性名
     * @return 值
     */
    public Long getObjectFieldLong(String key, String fieldName) {
        Object result = redisTemplate.opsForHash().get(keyObject(key), fieldName);
        if (result instanceof Integer) {
            return ((Integer) result).longValue();
        }

        return (Long) result;
    }

    /**
     * 获取对象属性(int类型属性)
     * @param key 健
     * @param fieldName 属性名
     * @return 值
     */
    public Integer getObjectFieldInteger(String key, String fieldName) {
        Object result = redisTemplate.opsForHash().get(keyObject(key), fieldName);
        if (result instanceof Long) {
            return ((Long) result).intValue();
        }

        return (Integer) result;
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
     * @return 完整的key(即末尾追加类型)
     */
    private String keyList(String key) {
        return RegexUtil.find("\\.list$", key) ? key : key + ".list";
    }

    /**
     * 存入list(尾部追加)
     * @param key 键
     * @param list 集合
     */
    public void putList(String key, List<?> list) {
        removeList(key);
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 存入list(尾部追加)
     * @param key 键
     * @param list 集合
     * @param durationSecond 时长(单位: 秒)
     */
    public void putList(String key, List<?> list, long durationSecond) {
        removeList(key);
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), durationSecond);
    }

    /**
     * 为list添加元素(尾部追加)
     * @param key 键
     * @param val 值
     */
    public void addListItem(String key, Object val) {
        redisTemplate.opsForList().rightPush(keyList(key), val);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 为list添加元素(尾部追加)
     * @param key 键
     * @param val 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void addListItem(String key, Object val, long durationSecond) {
        redisTemplate.opsForList().rightPush(keyList(key), val);
        expire(keyList(key), durationSecond);
    }

    /**
     * 为list批量添加元素(尾部追加)
     * @param key 键
     * @param list 元素集合
     */
    public void addListItems(String key, List<?> list) {
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 为list批量添加元素(尾部追加)
     * @param key 键
     * @param list 元素集合
     * @param durationSecond 时长(单位: 秒)
     */
    public void addListItems(String key, List<?> list, long durationSecond) {
        redisTemplate.opsForList().rightPushAll(keyList(key), list);
        expire(keyList(key), durationSecond);
    }

    /**
     * 在list首部添加元素
     * @param key 键
     * @param val 值
     */
    public void offerListFirst(String key, Object val) {
        redisTemplate.opsForList().leftPush(keyList(key), val);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 在list首部添加元素
     * @param key 键
     * @param val 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void offerListFirst(String key, Object val, long durationSecond) {
        redisTemplate.opsForList().leftPush(keyList(key), val);
        expire(keyList(key), durationSecond);
    }

    /**
     * 在list尾部添加元素
     * @param key 键
     * @param val 值
     */
    public void offerListLast(String key, Object val) {
        redisTemplate.opsForList().rightPush(keyList(key), val);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 在list尾部添加元素
     * @param key 键
     * @param val 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void offerListLast(String key, Object val, long durationSecond) {
        redisTemplate.opsForList().rightPush(keyList(key), val);
        expire(keyList(key), durationSecond);
    }

    /**
     * 获取list
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
     * 根据索引获取list元素
     * @param key 键
     * @param index 索引
     * @return
     */
    public Object getListItemByIndex(String key, long index) {
        return redisTemplate.opsForList().index(keyList(key), index);
    }

    /**
     * 获取list的某一页
     * @param key 键
     * @param start 开始位置
     * @param stop 结束位置(包含尾)
     * @return
     */
    public List<?> getListPageItems(String key, long start, long stop) {
        return redisTemplate.opsForList().range(keyList(key), start, stop);
    }

    /**
     * 移除并返回第一个元素
     * @param key 键
     * @return
     */
    public Object pollListFirst(String key) {
        return redisTemplate.opsForList().leftPop(keyList(key));
    }

    /**
     * 移除并返回最后一个元素
     * @param key 键
     * @return
     */
    public Object pollListLast(String key) {
        return redisTemplate.opsForList().rightPop(keyList(key));
    }

    /**
     * 删除list集合
     * @param key 键
     */
    public void removeList(String key) {
        redisTemplate.delete(keyList(key));
    }

    /**
     * 根据索引移除list元素
     * @param key 键
     * @param index 索引
     */
    public void removeListItemByIndex(String key, long index) {
        String tmpStr = "__list_delete__";
        redisTemplate.opsForList().set(keyList(key), index, tmpStr);
        redisTemplate.opsForList().remove(keyList(key), index, tmpStr);
    }

    /**
     * 移除所有指定值的list元素
     * @param key 键
     * @param val 值
     */
    public void removeListItemsByValue(String key, Object val) {
        redisTemplate.opsForList().remove(keyList(key), 0, val);
    }

    /**
     * 根据指定数量移除所有指定值的list元素
     * @param key 键
     * @param val 值
     * @param amount 数量
     * @param left 从左开始
     */
    public void removeListItemsByValue(String key, Object val, long amount, boolean left) {
        try {
            if (amount <= 0) throw new RuntimeException("删除list元素指定的amount必须大于0");
            if (!left) amount = -amount;
            redisTemplate.opsForList().remove(keyList(key), amount, val);
        } catch (Exception e) {
            LoggerUtil.printStackTrace(logger, e);
            throw new RuntimeException("根据值删除list元素出错: " + e.getMessage(), e);
        }
    }

    /**
     * 只保留list该区间的元素
     * @param key 键
     * @param start 开始位置(包含首)
     * @param stop 结束位置(包含尾)
     */
    public void trimList(String key, long start, long stop) {
        redisTemplate.opsForList().trim(keyList(key), start, stop);
    }

    /**
     * 设置list的过期时间
     * @param key 键
     * @param durationSecond 时长(单位: 秒)
     */
    public void expireList(String key, long durationSecond) {
        redisTemplate.expire(keyList(key), durationSecond, TimeUnit.SECONDS);
    }

    //================================set
    /**
     * 补充set的key
     * @param key 键
     * @return 完整的key(即末尾追加类型)
     */
    private String keySet(String key) {
        return RegexUtil.find("\\.set$", key) ? key : key + ".set";
    }

    /**
     * 存入set
     * @param key 键
     * @param set 集合
     */
    public void putSet(String key, Set<?> set) {
        removeSet(key);
        redisTemplate.opsForSet().add(keySet(key), set.toArray());
        expire(keyList(key), defaultDuration);
    }

    /**
     * 存入set
     * @param key 键
     * @param set 集合
     * @param durationSecond 时长(单位: 秒)
     */
    public void putSet(String key, Set<?> set, long durationSecond) {
        removeSet(key);
        redisTemplate.opsForSet().add(keySet(key), set.toArray());
        expire(keyList(key), durationSecond);
    }

    /**
     * 为set添加元素
     * @param key 键
     * @param val 值
     */
    public void addSetItem(String key, Object val) {
        redisTemplate.opsForSet().add(keySet(key), val);
        expire(keyList(key), defaultDuration);
    }

    /**
     * 为set添加元素
     * @param key 键
     * @param val 值
     * @param durationSecond 时长(单位: 秒)
     */
    public void addSetItem(String key, Object val, long durationSecond) {
        redisTemplate.opsForSet().add(keySet(key), val);
        expire(keyList(key), durationSecond);
    }

    /**
     * 为set添加集合
     * @param key 键
     * @param set 集合
     */
    public void addSetItems(String key, Set<?> set) {
        redisTemplate.opsForSet().add(keySet(key), set.toArray());
        expire(keyList(key), defaultDuration);
    }

    /**
     * 为set添加集合
     * @param key 键
     * @param set 集合
     * @param durationSecond 时长(单位: 秒)
     */
    public void addSetItems(String key, Set<?> set, long durationSecond) {
        redisTemplate.opsForSet().add(keySet(key), set.toArray());
        expire(keyList(key), durationSecond);
    }

    /**
     * set集合大小
     * @param key 键
     * @return 大小
     */
    public long getSetLength(String key) {
        return redisTemplate.opsForSet().size(keySet(key));
    }

    /**
     * 获取set集合
     * @param key 键
     * @return
     */
    public Set<?> getSet(String key) {
        return redisTemplate.opsForSet().members(keySet(key));
    }

    /**
     * 删除set集合
     * @param key 键
     */
    public void removeSet(String key) {
        redisTemplate.delete(keySet(key));
    }

    /**
     * set集合是否包含某值
     * @param key 键
     * @param val 值
     * @return 是否包含
     */
    public boolean containsSet(String key, Object val) {
        return redisTemplate.opsForSet().isMember(keySet(key), val);
    }

    /**
     * 设置set的过期时间
     * @param key 键
     * @param durationSecond 时长(单位: 秒)
     */
    public void expireSet(String key, long durationSecond) {
        redisTemplate.expire(keySet(key), durationSecond, TimeUnit.SECONDS);
    }

    //================================sorted set
    /**
     * 补充sorted set的key
     * @param key 键
     * @return 完整的key(即末尾追加类型)
     */
    private String keyZSet(String key) {
        return RegexUtil.find("\\.sortedSet$", key) ? key : key + ".sortedSet";
    }

    /**
     * 存入sorted set
     * @param key 键
     * @param set set
     * @param <T> 值范型
     */
    public <T> void putZSet(String key, Set<ZSetOperations.TypedTuple<T>> set) {
        removeZSet(keyZSet(key));
        redisTemplate.opsForZSet().add(keyZSet(key), set);
        expire(keyZSet(key), defaultDuration);
    }

    /**
     * 存入sorted set
     * @param key 键
     * @param set set
     * @param durationSecond 时长(单位: 秒)
     * @param <T> 值范型
     */
    public <T> void putZSet(String key, Set<ZSetOperations.TypedTuple<T>> set, long durationSecond) {
        removeZSet(keyZSet(key));
        redisTemplate.opsForZSet().add(keyZSet(key), set);
        expire(keyZSet(key), durationSecond);
    }

    /**
     * 为sorted set添加元素
     * @param key 键
     * @param val 值
     * @param score 分数
     */
    public void addZSetItem(String key, Object val, double score) {
        redisTemplate.opsForZSet().add(keyZSet(key), val, score);
        expire(keyZSet(key), defaultDuration);
    }

    /**
     * 为sorted set添加元素
     * @param key 键
     * @param val 值
     * @param score 分数
     * @param durationSecond 时长(单位: 秒)
     */
    public void addZSetItem(String key, Object val, double score, long durationSecond) {
        redisTemplate.opsForZSet().add(keyZSet(key), val, score);
        expire(keyZSet(key), durationSecond);
    }

    /**
     * 获取sorted set
     * @param key 键
     * @param <T> 值范型
     * @return sorted set
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> getZSet(String key) {
        return redisTemplate.opsForZSet().rangeWithScores(keyZSet(key), 0, -1);
    }

    /**
     * 获取一页sorted set
     * @param key 键
     * @param start 启始位置
     * @param end 结束位置(包含)
     * @param <T> 值范型
     * @return 一页sorted set
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> getZSetLimit(String key, long start, long end) {
        return redisTemplate.opsForZSet().rangeWithScores(keyZSet(key), start, end);
    }

    /**
     * 获取sorted set(倒序)
     * @param key 键
     * @param <T> 值范型
     * @return sorted set
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> getZSetDesc(String key) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(keyZSet(key), 0, -1);
    }

    /**
     * 获取一页sorted set(倒序)
     * @param key 键
     * @param start 启始位置
     * @param end 结束位置(包含)
     * @param <T> 值范型
     * @return 一页sorted set
     */
    public <T> Set<ZSetOperations.TypedTuple<T>> getZSetLimitDesc(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(keyZSet(key), start, end);
    }

    /**
     * 获取sorted set大小
     * @param key 键
     * @return 大小
     */
    public long getZSetLength(String key) {
        return redisTemplate.opsForZSet().size(keyZSet(key));
    }

    /**
     * 删除sorted set
     * @param key 键
     */
    public void removeZSet(String key) {
        redisTemplate.opsForZSet().removeRange(keyZSet(key), 0, -1);
    }

    /**
     * 删除sorted set的元素
     * @param key 键
     * @param val 值
     */
    public void removeZSetItem(String key, Object val) {
        redisTemplate.opsForZSet().remove(keyZSet(key), val);
    }

    /**
     * 删除sorted set的指定多个元素
     * @param key 键
     * @param val 值
     */
    public void removeZSetItems(String key, Object... val) {
        redisTemplate.opsForZSet().remove(keyZSet(key), val);
    }

    /**
     * 设置sorted set的过期时间
     * @param key 键
     * @param durationSecond 时长(单位: 秒)
     */
    public void expireZSet(String key, long durationSecond) {
        redisTemplate.expire(keyZSet(key), durationSecond, TimeUnit.SECONDS);
    }

}
