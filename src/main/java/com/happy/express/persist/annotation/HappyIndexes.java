package com.happy.express.persist.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 表索引集合
 * @author: liliwen
 * @date: 2021-01-17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HappyIndexes {

    HappyIndex[] indexes() default {};

    HappyUniqueIndex[] uniqueIndexes() default {};

    @Target({})
    @Retention(RetentionPolicy.RUNTIME)
    @interface HappyIndex {
        String[] fields();
        String suffix() default "";//后缀名(idx_suffix)
    }

    @Target({})
    @Retention(RetentionPolicy.RUNTIME)
    @interface HappyUniqueIndex {
        String[] fields();
        String suffix() default "";//后缀名(idx_suffix)
    }

}
