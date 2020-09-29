package com.happy.express.code;

import lombok.Data;

/**
 * @description: 属性映射
 * @author: llw
 * @date: 2020-08-19
 */
@Data
public class PropertiesMapping {

    /**mysql列名*/
    private String col;
    /**java字段名*/
    private String prop;

}
