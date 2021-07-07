package com.happy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 分页持数据传输对象(继承ArrayList是为了分页插件可以返回结果)
 * @author: llw
 * @date: 2016-11-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PagingDto<T> extends ArrayList<T> {

    /** 分页条件查询出的实体 */
    private List<T> entities;

    /** 条件查询出的总数 */
    private long count;

}
