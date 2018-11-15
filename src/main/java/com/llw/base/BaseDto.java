package com.llw.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @discription: 数据传输对象基类
 * @author: llw
 * @date: 2018-11-07
 */
@ApiModel("数据传输对象基类")
@Data
public class BaseDto implements Comparable {

    @ApiModelProperty("id")
    private long id;

    @Override
    public int compareTo(Object o) {
        if (o instanceof BaseDto) {
            BaseDto model = (BaseDto) o;
            if (this.id < model.id) return -1;
            if (this.id > model.id) return 1;
            return 0;
        }

        return 1;
    }

}
