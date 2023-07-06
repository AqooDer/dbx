package com.dbx.core.db.sql.generator;

import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDataSourceException;
import com.dbx.core.db.data.CreateTableSqLInfo;
import com.dbx.core.exception.JobDefinitionException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Aqoo
 */
@Slf4j
public abstract class AbstractSqlGenerator implements SqlGenerator {

    protected static final String CR = System.getProperty("line.separator");

    public static final String DROP_TABLE_SQL = "DROP TABLE IF EXISTS %s;";

    public static final String INSERT_SQL_TEMPLATE = "INSERT INTO %s(%s) VALUES (%s)";
    public static final String CLEAR_TABLE_SQL_TEMPLATE = " DELETE FROM %s ";


    @Override
    public CreateTableSqLInfo getCreateTableSql(TableModel tableModel) throws JobDataSourceException {
        if (tableModel == null) {
            throw new JobDefinitionException("param tableModel is null.");
        }
        return doGenerateCreateTableSql(tableModel);
    }

    @Override
    public String getDropTableSql(TableModel tableModel) {
        String dropSql = doGetDropTableSql(tableModel);
        return dropSql == null ? String.format(DROP_TABLE_SQL, tableModel.getTableName()) : dropSql;
    }

    @Override
    public String getClearTableSql(String tableName) {
        return String.format(CLEAR_TABLE_SQL_TEMPLATE, tableName);
    }

    @Override
    public String getWriteInsertSql(TableModel tableModel, List<String> fields, List<String> values) {
        StringBuilder sb = new StringBuilder();
        if (fields.size() != values.size()) {
            throw new JobDataSourceException(" the field length must match the values length.");
        }
        String sql = doGetInsertSql(tableModel, fields, values);
        if (sql == null) {
            sb.append(String.format(INSERT_SQL_TEMPLATE, tableModel.getTableName(), String.join(",", fields), String.join(",", values)));
            return sb.toString();
        }
        return sql;
    }

    @Override
    public String getExecInsertSql(TableModel tableModel, List<String> fields) {
        List<String> str = fields.stream().map(v -> "?").collect(Collectors.toList());
        return String.format(INSERT_SQL_TEMPLATE, tableModel.getTableName(), String.join(",", fields), String.join(",", str));
    }


    /**
     * 根据字段配置处理生成建表DDL语句
     *
     * @param tableModel 表定义模型
     * @return 建表sql语句
     */
    protected abstract CreateTableSqLInfo doGenerateCreateTableSql(TableModel tableModel);

    protected abstract String doGetDropTableSql(TableModel tableModel);

    protected abstract String doGetInsertSql(TableModel tableModel, List<String> fields, List<String> values);
}
