package com.happy.express.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 分表dao
 * @author: llw
 * @date: 2020-08-24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HappySplitTableDao {

    public String tableName() default "";

    public String[] methods();

}