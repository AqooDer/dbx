package com.dbx.bean.config.annotation;

import com.dbx.core.constans.FieldJavaType;

import java.lang.annotation.*;

/**
 * @author Aqoo
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DdlConfig {
    /**
     * @return
     */
    FieldJavaType type() default FieldJavaType.NONE;

    /**
     * @return
     */
    int length() default 0;

    /**
     * 小数位 浮点型或者科学技术法使用
     *
     * @return
     */
    int scale() default 0;

    /**
     * 备注， 这里强制一下必须写
     *
     * @return
     */
    String content() default "";

    /**
     * 是否是主键值
     *
     * @return
     */
    boolean primary() default false;

    /**
     * 是否可以为空
     *
     * @return
     */
    boolean nullable() default true;

    /**
     * 默认值设置
     *
     * @return
     */
    String defaultValue() default "";
}
