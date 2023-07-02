package com.dbx.bean.config.annotation;

import java.lang.annotation.*;

/**
 * 在多个相同配置时，需要使用该注解指定
 *
 * @author Aqoo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UseDdl {
}
