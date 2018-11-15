package com.llw.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @discription: vo基类
 * @author: llw
 * @date: 2016-11-26
 */
@ApiModel(value = "响应值对象")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultVo<T> {

    @ApiModelProperty(value = "状态(0:失败 1:成功)")
    protected int status;

    @ApiModelProperty(value = "消息")
    protected String message;

    @ApiModelProperty(value = "返回数据")
    protected T result;

}
