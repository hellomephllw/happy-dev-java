package com.happy.base;

import java.util.List;

/**
 * @description: mapper基类
 * @author: liliwen
 * @date: 2021-01-16
 */
public interface BaseMapper<T> {

    public void add(T entity);

    public void addBatch(List<T> list);

    public void remove(int id);

    public void removeByIds(List<Integer> ids);

    public void update(T entity);

    public void updateBatch(List<T> list);

    public T get(int id);

    public List<T> findAll();

    public List<T> findByIds(List<Integer> ids);

}
