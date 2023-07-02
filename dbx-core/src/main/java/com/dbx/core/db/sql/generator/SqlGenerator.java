package com.dbx.core.db.sql.generator;


import com.dbx.core.db.data.CreateTableSqLInfo;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDataSourceException;

import java.util.List;

/**
 * sql语句生成器
 *
 * @author Aqoo
 */
public interface SqlGenerator {
    /**
     * 根据字段配置处理生成建表DDL语句
     *
     * @param tableModel 表定义模型
     * @return 建表sql语句
     * @throws JobDataSourceException 执行异常
     */
    CreateTableSqLInfo getCreateTableSql(TableModel tableModel) throws JobDataSourceException;


    String getDropTableSql(TableModel tableModel);

    String getClearTableSql(String tableName);

    /**
     * 返回分页查询sql语句
     *
     * @param tableModel 表定义模型
     * @param start      开始位
     * @param length     数据查询长度
     * @return 分页查询sql语句
     */
    String getSegmentDataQuerySql(TableModel tableModel, int start, int length);

    /**
     * 获取数据库表信息查询sql语句，主要查询 TABLE_NAME REMARKS 两个字段的值。
     * <p>
     * 注意：
     * <p>
     * 1.返回值的列表必须有以上两项：TABLE_NAME（表名） REMARKS （表备注）
     * <p>
     * 2.如果使用默认查询：getConnection().getMetaData().getTables() 。 请返回null。
     *
     * @param tableName 表名
     * @return 查询表信息sql
     */
    String getTableModelQuerySql(String tableName);

    /**
     * 获取数据库表的列定义信息 查询sql语句，主要查询以下字段：
     * <p>
     * COLUMN_NAME 列名
     * <p>
     * TYPE_NAME 列类型
     * <p>
     * COLUMN_SIZE 列长度
     * <p>
     * DECIMAL_DIGITS 列小数
     * <p>
     * NULLABLE 不是null 0 不允许 1 允许
     * <p>
     * COLUMN_DEF 列默认值
     * <p>
     * REMARKS 列备注
     * <p>
     * 如果使用默认查询：getConnection().getMetaData().getColumns() 。 请返回null。
     *
     * @param tableName 表名
     * @return 查询表列信息sql
     */
    String getFieldModelQuerySql(String tableName);

    /**
     * 生成 insert sql
     * 注意：fields.length = values.length 并且顺序一致
     *
     * @param tableModel 表模型
     * @param fields     字段列表
     * @param values     值
     * @return 返回脚本sql
     */
    String getWriteInsertSql(TableModel tableModel, List<String> fields, List<String> values);

    /**
     * 获取执行sql
     *
     * @param tableModel 表模型
     * @param fields     字段列表
     * @return 返回执行sql
     */
    String getExecInsertSql(TableModel tableModel, List<String> fields);


}
