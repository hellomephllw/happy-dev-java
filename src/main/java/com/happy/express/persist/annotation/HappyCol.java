package com.happy.express.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HappyCol {

    public int len() default 0;

    public boolean nullable() default true;

    public boolean unique() default false;

    public int precision() default 0;

    public int scale() default 0;

    public boolean text() default false;

}
