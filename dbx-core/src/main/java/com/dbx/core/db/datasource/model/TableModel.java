package com.dbx.core.db.datasource.model;

import lombok.Data;

import java.util.Map;


/**
 * 表信息
 * schema catalog tableType 目前无用信息
 *
 * @author Aqoo
 */
@Data
public class TableModel {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 备注信息
     */
    private String content;

    /**
     * 属于表的字段信息
     * key : fieldName
     */
    private Map<String, FieldModel> fieldModels;

    /**
     * 索引信息
     * key : fieldName1,fieldName2
     */
    private Map<String, IndexModel> indexModels;


    private TableModel source;

    /**
     * 表模式，一般理解为Oracle的用户名
     */
    private String schema;
    /**
     * 理解为库名即可
     */
    private String catalog;

    /**
     * 表类型,一般是 TABLE 或 VIEW
     * <p>
     * 暂未使用
     */
    private String tableType;

}
