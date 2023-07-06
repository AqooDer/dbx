package com.dbx.bean.config.annotation;

import com.dbx.bean.config.support.ValueDefaultType;

import java.lang.annotation.*;

/**
 * @author Aqoo
 * ddl设置关系： ddl > SUPER_SOURCE/SUPER_TARGET > SOURCE
 * value设置关系 ： customFormatValue > defaultFormatValue > defaultValue > ref >  SOURCE
 * 主键id的设置关系：id > ddl > 其他引用
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = MapperFields.class)
public @interface MapperField {

    String target();

    String source() default "";

    /**
     * 值引用和ddl引用。
     * 配置依然大于引用
     * 引用父类中的source table中的值和ddl定义
     * superSource和superTarget 只能同时存在一个有值
     *
     * @return
     */
    String superSource() default "";

    /**
     * 引用父类中的source table中的值和ddl定义
     *
     * @return
     */
    String superTarget() default "";

    /**
     * 默认生成值
     *
     * @return
     */
    String defaultValue() default "";

    /**
     * 平台提供的默认格式化数据的方法
     *
     * @return
     */
    ValueDefaultType defaultFormatValue() default ValueDefaultType.NONE;


    DdlConfig ddl() default @DdlConfig();

    /**
     * 指定field是否是id
     * 类配置只能有一个配置该值为true 。其值代表意义等同于：ddl = @DdlConfig(dataType = "varchar(64)", content = "主键id" , primary=true , nullable = false)
     *
     * @return
     */
    boolean id() default false;
}
