package com.dbx.core.db.datasource.model;

import com.dbx.core.constans.SortType;
import lombok.Data;

/**
 * 索引模型
 */
@Data
public class IndexModel {
    private String indexName;

    private String[] fieldNames = new String[0];

    /**
     * 默认 ASC 和 fieldNames的长度保持一致。
     */
    private SortType[] sortType = new SortType[0];

    private boolean unique;


}
