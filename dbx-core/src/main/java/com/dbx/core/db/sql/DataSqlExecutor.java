package com.dbx.core.db.sql;

import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.exception.JobException;

import java.util.List;
import java.util.Map;

/**
 * 数据处理执行器
 * 1.查询源表信息
 * 2.根据定义信息和源表信息生成sql语句等。
 *
 * @author Aqoo
 */
public interface DataSqlExecutor {
    int BATCH_SQL_COUNT = 1000;

    /**
     * 默认批量处理大小
     *
     * @return 大小
     */
    default int getBatchSqlCount() {
        return BATCH_SQL_COUNT;
    }

    /**
     * 批量生成sql语句
     *
     * @param tableValueMapperDefinition 表数据定义
     * @param counter                    查询数据量的大小
     * @return 返回目标表的sql数组
     * @throws JobException 转换异常
     */
    DataSqlObject generatorInsertSql(TableRowValueMapperDefinition tableValueMapperDefinition, int counter) throws JobException;

    /**
     * 更具sql保存数据
     *
     * @param dataSqlObject 数据sql
     * @throws JobException 数据转换异常
     */
    void batchSaveData(DataSqlObject dataSqlObject) throws JobException;

    void batchClearTables(String[] sqlList);


    /**
     * 分段查询数据，
     *
     * @param tableValueMapperDefinition 表数据定义
     * @param num                        数据段数
     * @param length                     数据单次查询个数
     * @return 原始数据
     * @throws JobException 数据转换异常
     */
    List<Map<String, Object>> querySegmentDataFromSource(TableRowValueMapperDefinition tableValueMapperDefinition, int num, int length) throws JobException;


}
