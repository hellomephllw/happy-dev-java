package com.happy.base;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @description: 模型对象基类，所有实体都要继承该基类
 * @author: happy
 * @date: 2016-11-17
 */
@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    /** 逻辑主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;

    @Override
    public int compareTo(Object o) {
        if (o instanceof BaseEntity) {
            BaseEntity model = (BaseEntity) o;
            if (id < model.id) return -1;
            if (id > model.id) return 1;
            return 0;
        }

        return 1;
    }
}
