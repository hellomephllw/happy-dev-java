package com.happy.base;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @description: 持久层传输对象基类
 * @author: llw
 * @date: 2019-05-24
 */
@MappedSuperclass
@Data
public class BasePo implements Comparable {

    @Id
    protected int id;

    @Override
    public int compareTo(Object o) {
        if (o instanceof BasePo) {
            BasePo model = (BasePo) o;
            if (this.id < model.id) return -1;
            if (this.id > model.id) return 1;
            return 0;
        }

        return 1;
    }

}
