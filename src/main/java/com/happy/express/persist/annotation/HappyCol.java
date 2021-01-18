package com.happy.express.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 字段
 * @author: llw
 * @date: 2020-08-19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HappyCol {

    int len() default 0;

    boolean fixLen() default false;

    boolean nullable() default true;

    boolean unique() default false;

    int precision() default 0;

    int scale() default 0;

    boolean text() default false;

    boolean longText() default false;

}
