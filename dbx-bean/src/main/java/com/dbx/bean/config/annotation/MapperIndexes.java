package com.dbx.bean.config.annotation;

import java.lang.annotation.*;

/**
 * @author zhaolangjing
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperIndexes {
    MapperIndex[] value();
}
