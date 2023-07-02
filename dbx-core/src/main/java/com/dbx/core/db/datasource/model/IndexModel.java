package com.dbx.core.db.datasource.model;

import com.dbx.core.constans.SortType;

/**
 * 索引模型
 */
public class IndexModel {
    private String indexName;

    private String[] fieldNames;

    /**
     * 默认 ASC 和 fieldNames的长度保持一致。
     */
    private SortType[] sortType;

    private boolean unique;
}
