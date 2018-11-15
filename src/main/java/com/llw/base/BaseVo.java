package com.llw.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @discription: 值对象基类
 * @author: llw
 * @date: 2018-10-26
 */
@ApiModel("值对象基类")
@Data
public abstract class BaseVo implements Comparable {

    @ApiModelProperty("id")
    protected long id;

    @Override
    public int compareTo(Object o) {
        if (o instanceof BaseEntity) {
            BaseEntity model = (BaseEntity) o;
            if (this.id < model.id) return -1;
            if (this.id > model.id) return 1;
            return 0;
        }

        return 1;
    }

}
