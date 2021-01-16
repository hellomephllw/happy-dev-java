package com.happy.base;

import java.util.List;

/**
 * @description: mapper基类
 * @author: liliwen
 * @date: 2021-01-16
 */
public interface BaseMapper<T> {

    public void add(T entity) throws Exception;

    public void addBatch(List<T> list) throws Exception;

    public void remove(int id) throws Exception;

    public void removeByIds(List<Integer> ids) throws Exception;

    public void update(T entity) throws Exception;

    public void updateBatch(List<T> list) throws Exception;

    public T get(int id) throws Exception;

    public List<T> findAll() throws Exception;

    public List<T> findByIds(List<Integer> ids) throws Exception;

    public int count() throws Exception;

}
