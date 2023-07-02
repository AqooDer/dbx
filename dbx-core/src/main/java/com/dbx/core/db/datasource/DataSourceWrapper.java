package com.dbx.core.db.datasource;


import com.dbx.core.constans.DbType;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.db.sql.generator.SqlGenerator;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.exception.JobExecuteException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Aqoo
 */
public interface DataSourceWrapper {

    /**
     * 获取注入到上下文中的 数据源
     *
     * @return DataSource
     */
    JobDataSource getDataSource();

    /**
     * 获取某个表的字段定义
     *
     * @param tableName 表名称
     * @return List<FieldDbModel> 返回一个表的列定义
     */
    List<FieldDbModel> getFieldDbModel(String tableName, SqlGenerator sqlGenerator);

    /**
     * 获取tableModel的信息
     *
     * @return 表信息
     */
    TableModel getTableModel(String tableName, FieldTypeResolver fieldTypeResolver, SqlGenerator sqlGenerator);

    /**
     * 返回当前数据库链接的数据库类型
     *
     * @return DbType
     */
    DbType getDbType();

    /**
     * 事务执行sql
     *
     * @param function 回掉函数
     * @param <T>      返回对象
     * @return 返回一个T对象
     * @throws JobExecuteException sql执行异常
     */
    <T> T executeInTx(Function<JdbcTemplate, T> function) throws JobExecuteException;

    /**
     * 不再事物中之心sql
     *
     * @param function 回掉函数
     * @param <T>      返回对象
     * @return 返回一个T对象
     * @throws JobExecuteException sql执行异常
     */
    <T> T execute(Function<JdbcTemplate, T> function) throws JobExecuteException;

    /**
     * @param consumer 回掉函数
     * @throws JobExecuteException sql执行异常
     */
    void accept(Consumer<JdbcTemplate> consumer) throws JobExecuteException;
}
