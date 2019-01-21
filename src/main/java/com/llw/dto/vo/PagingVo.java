package com.llw.dto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @description: 分页值对象
 * @author: llw
 * @date: 2018-11-15
 */
@ApiModel("分页值对象")
@Data
public class PagingVo<T> {

    @ApiModelProperty("当前页码")
    private int pageNo;

    @ApiModelProperty("每页数据量")
    private int pageSize;

    @ApiModelProperty("总页数")
    private int pageAmount;

    @ApiModelProperty("总数据量")
    private long total;

    /** 实体集合:建议使用TreeSet */
    @ApiModelProperty("实体集合")
    private Set<T> entities;

}
