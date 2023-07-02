package com.dbx.core.db.datasource.model;

import com.dbx.core.constans.FieldType;
import lombok.Builder;
import lombok.Data;

/**
 * 用于中间处理field的状态
 *
 * @author Aqoo
 */
@Data
@Builder
public class FieldModel {
    /**
     * 数据库模型 字段配置
     */
    private FieldDbModel fieldDbModel;

    /**
     * java模型 字段配置
     */
    private FieldJavaModel fieldJavaModel;

    /**
     * source来源 0 本身， 1 superSource 2
     */
    private FieldType sourceFrom;

    /**
     * 原始数据模型来源
     */
    private FieldModel source;
}
