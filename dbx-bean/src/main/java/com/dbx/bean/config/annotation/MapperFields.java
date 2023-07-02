package com.dbx.bean.config.annotation;

import java.lang.annotation.*;

/**
 * @author Aqoo
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperFields {
    MapperField[] value();
}
