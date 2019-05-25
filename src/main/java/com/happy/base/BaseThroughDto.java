package com.happy.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * @description: 贯穿的数据传输对象基类
 * @author: llw
 * @date: 2019-05-25
 */
@ApiModel("贯穿的数据传输对象基类")
@MappedSuperclass
@Data
public class BaseThroughDto implements Comparable {

    @ApiModelProperty("id")
    @Id
    private int id;

    @Override
    public int compareTo(Object o) {
        if (o instanceof BaseThroughDto) {
            BaseThroughDto model = (BaseThroughDto) o;
            if (this.id < model.id) return -1;
            if (this.id > model.id) return 1;
            return 0;
        }

        return 1;
    }

}
