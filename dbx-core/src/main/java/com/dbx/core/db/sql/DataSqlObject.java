package com.dbx.core.db.sql;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据sql执行存储对象
 *
 * @author Aqoo
 */
@Data
public class DataSqlObject {

    private List<String> writeSqls = new ArrayList<>();

    private String execSql;

    private List<Object[]> batchArgs = new ArrayList<>();

    /**
     * tvmId , 子项数据
     */
    private Map<String, DataSqlObject> children = new HashMap<>();

}
