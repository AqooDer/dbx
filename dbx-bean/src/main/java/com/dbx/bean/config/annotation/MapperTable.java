package com.dbx.bean.config.annotation;

import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.db.data.ValueContext;
import com.dbx.core.db.data.ValueFormat;

import java.lang.annotation.*;

/**
 * 在使用时，mapper分为几种：
 * 1.
 *
 * @author Aqoo
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MapperTable {
    /**
     * 目标表的表名称
     *
     * @return
     */
    String target();

    /**
     * 源表的表名称
     *
     * @return
     */
    String source() default "";

    /**
     * 排除不需要导入的字段
     * 注意：
     * 当字段名称发生了变化之后，需要用 MapperField 定义，同时需要用 excludeFields排除原始字段，否者将生成两个字段。
     *
     * @return
     */
    String[] excludeFields() default {};

    /**
     * 需要导入的字段
     * 该字段使用为：不需要数据处理或者字段名不需要变化的情况
     * 需要的请使用 MapperField
     *
     * @return
     * @see MapperField
     */
    String[] includeFields() default {};

    /**
     * child 列表
     *
     * @return
     */
    Class<?>[] children() default {};

    /**
     * 格式化数据类
     *
     * @return
     * @see ValueFormat#format(TableFieldValueMapperDefinition, ValueContext) ；
     */
    Class<? extends ValueFormat> customFormatValue() default ValueFormat.class;

    /**
     * 表备注信息
     *
     * @return
     */
    String content() default "";
}
