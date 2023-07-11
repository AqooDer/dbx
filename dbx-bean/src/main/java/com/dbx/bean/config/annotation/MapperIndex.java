package com.dbx.bean.config.annotation;

import com.dbx.core.constans.SortType;

import java.lang.annotation.*;

/**
 * 创建索引的描述
 * 系统索引名称，规则：${column_name}_idx 排序 asc
 * 系统将根据 columnName 来判断是否是同一个索引 。
 *
 * @author zhaolangjing
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(value = MapperIndexes.class)
public @interface MapperIndex {

    /**
     * 排序方式 ，默认为正序排序
     * sort只能为空 或者  sort.length = columnNames.length
     * <p>
     * 值：A 正序排列  D 倒序排列
     *
     * @return
     */
    SortType[] sort() default {SortType.ASC};


    /**
     * 是否是唯一索引
     * 唯一索引要求 索引的值不能重复，请注意。
     *
     * @return
     */
    boolean unique() default false;

    /**
     * 备注信息
     *
     * @return
     */
    String remarks() default "";

}
