package com.happy.base;

import com.happy.express.persist.annotation.HappyId;
import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @description: 实体基类
 * @author: llw
 * @date: 2020-08-19
 */
@MappedSuperclass
@Data
public class BaseHappyEntity implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    /**物理主键*/
    @HappyId
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
