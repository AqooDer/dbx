package com.dbx.core.config;

import com.dbx.core.db.datasource.model.FieldModel;

import java.util.List;

/**
 * @author Aqoo
 */
public interface TableFieldValueMapperDefinition {
    /**
     * 获取目标字段定义名称
     *
     * @return 目标字段定义名称
     */
    String getTargetField();

    /**
     * 获取该字段的表定义信息
     * 注意：
     * 子表不能有"source"字段。
     *
     * @return 该字段的表定义信息
     */
    FieldModel getTargetFieldModel();

    /**
     * 获取数据格式化方式：返回一个list，循环匹配，当一个条件满足时，不再往下执行。
     *
     * @return 数据格式化方式
     */
    List<FieldValueFormatDefinition> getValueFormatDefinition();

}
