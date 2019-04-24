package com.happy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 分页持数据传输对象
 * @author: llw
 * @date: 2016-11-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingDto<T> {

    /** 分页条件查询出的实体 */
    private List<T> entities;

    /** 条件查询出的总数 */
    private long count;

}
