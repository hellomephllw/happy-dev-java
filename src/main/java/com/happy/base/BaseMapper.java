package com.happy.base;

import java.util.List;

/**
 * @description: mapper基类
 * @author: liliwen
 * @date: 2021-01-16
 */
public interface BaseMapper<T> {

    void add(T entity);

    void addBatch(List<T> list);

    void remove(int id);

    void removeByIds(List<Integer> ids);

    void update(T entity);

    void updateBatch(List<T> list);

    T get(int id);

    List<T> findAll();

    List<T> findByIds(List<Integer> ids);

}
