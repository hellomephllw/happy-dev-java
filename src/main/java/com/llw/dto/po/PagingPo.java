package com.llw.dto.po;

import lombok.Data;

import java.util.List;

/**
 * @discription: 分页持久层返回对象
 * @author: llw
 * @date: 2016-11-27
 */
@Data
public class PagingPo<T> {

    /** 分页条件查询出的实体 */
    private List<T> entities;

    /** 条件查询出的总数 */
    private long count;

}
