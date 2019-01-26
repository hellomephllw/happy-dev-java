package com.llw.dto.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 分页值对象
 * @author: llw
 * @date: 2019-01-26
 */
@ApiModel("分页值对象")
@Data
public class PagingVoNoSort<T> {

    @ApiModelProperty("当前页码")
    private int pageNo;

    @ApiModelProperty("每页数据量")
    private int pageSize;

    @ApiModelProperty("总页数")
    private int pageAmount;

    @ApiModelProperty("总数据量")
    private long total;

    @ApiModelProperty("实体集合")
    private List<T> entities;

}
