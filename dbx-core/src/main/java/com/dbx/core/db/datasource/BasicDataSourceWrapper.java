package com.dbx.core.db.datasource;

import com.dbx.core.constans.DbType;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.IndexModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.db.sql.generator.SqlGenerator;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.exception.JobDefinitionException;
import com.dbx.core.exception.JobExecuteException;
import com.dbx.core.util.jdbc.ResultSetPrinter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 包含事物的 DataSource包装器
 *
 * @author Aqoo
 */
@Slf4j
public class BasicDataSourceWrapper implements DataSourceWrapper {
    private final TransactionTemplate transactionTemplate;

    protected JdbcTemplate jdbcTemplate;

    protected JobDataSource dataSource;

    public BasicDataSourceWrapper(JobDataSource dataSource) {
        if (null == dataSource.getDbType()) {
            throw new JobDefinitionException("the DbTransferDataSource's dbType is null,please check. ");
        }
        DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
        definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public JobDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public List<FieldDbModel> getFieldDbModel(String tableName, SqlGenerator sqlGenerator) {
        try {
            return getFieldDbModel(tableName, sqlGenerator, this.getDataSource().getConnection());
        } catch (SQLException e) {
            throw new JobExecuteException(e);
        }
    }

    @Override
    public TableModel getTableModel(String tableName, FieldTypeResolver fieldTypeResolver, SqlGenerator sqlGenerator) {
        // 查询备注
        ResultSet rs = null;
        try {
            TableModel tableModel = new TableModel();
            tableModel.setTableName(tableName);
            String sql = sqlGenerator.getTableModelQuerySql(tableName);
            Connection connection = this.getDataSource().getConnection();
            if (sql != null && !sql.isEmpty()) {
                Map<String, Object> tableInfo = this.execute(jt -> jt.queryForMap(sql));
                if (tableInfo == null) {
                    throw new JobExecuteException(String.format("con not query the table '%s' info , please check sql '%s'.", tableName, sql));
                }
                tableModel.setContent(tableInfo.get("REMARKS").toString());
            } else {
                rs = connection.getMetaData().getTables(connection.getCatalog(), connection.getSchema(), tableName, null);

                while (rs.next()) {
                    tableModel.setContent(rs.getString("REMARKS"));
                }
            }
            tableModel.setCatalog(connection.getCatalog());
            tableModel.setSchema(connection.getSchema());
            tableModel.setTableType("TABLE");

            // 设置表的索引信息
            tableModel.setIndexModels(getAllIndex(tableName, connection));

            // 设置 FieldDbModel 数据。
            List<FieldDbModel> fieldDbModel = getFieldDbModel(tableName, sqlGenerator, connection);
            Map<String, FieldModel> fieldModels = new HashMap<>();
            for (FieldDbModel dbModel : fieldDbModel) {
                fieldTypeResolver.dbModel2JavaModel(dbModel);
                FieldModel fieldModel = FieldModel.builder().fieldDbModel(dbModel).fieldJavaModel(fieldTypeResolver.dbModel2JavaModel(dbModel)).build();
                fieldModels.put(dbModel.getFieldName(), fieldModel);
            }
            tableModel.setFieldModels(fieldModels);
            return tableModel;
        } catch (SQLException e) {
            throw new JobExecuteException(e);
        } finally {
            closeResultSet(rs);
        }
    }


    @Override
    public DbType getDbType() {
        return dataSource.getDbType();
    }

    /***
     * 在事务中执行sql
     */
    @Override
    public <T> T executeInTx(Function<JdbcTemplate, T> function) throws JobExecuteException {
        return transactionTemplate.execute(status -> function.apply(jdbcTemplate));
    }

    @Override
    public <T> T execute(Function<JdbcTemplate, T> function) throws JobExecuteException {
        return function.apply(jdbcTemplate);
    }

    @Override
    public void accept(Consumer<JdbcTemplate> consumer) throws JobExecuteException {
        consumer.accept(jdbcTemplate);
    }

    private List<FieldDbModel> getFieldDbModel(String tableName, SqlGenerator sqlGenerator, Connection connection) throws SQLException {
        List<FieldDbModel> list = new ArrayList<>();
        String primaryKey = getPrimaryKey(tableName, connection);
        if (primaryKey == null) {
            throw new JobExecuteException(String.format("The table '%s' does not exist or the primary key of the table does not exist , please check.", tableName));
        } else {
            primaryKey = primaryKey.toLowerCase();
        }
        String sql = sqlGenerator.getFieldModelQuerySql(tableName);
        if (sql != null && !sql.isEmpty()) {
            List<Map<String, Object>> fields = this.execute(jt -> jt.queryForList(sql));
            if (fields == null) {
                throw new JobExecuteException(String.format("the table '%s' did not query the columns , please check sql '%s'.", tableName, sql));
            }
            for (Map<String, Object> field : fields) {
                FieldDbModel dbModel = new FieldDbModel();
                dbModel.setFieldName(field.get("COLUMN_NAME").toString());
                dbModel.setPk(dbModel.getFieldName().equals(primaryKey));
                dbModel.setType(field.get("TYPE_NAME").toString());

                dbModel.setLength(Integer.parseInt(field.get("COLUMN_SIZE").toString()));
                dbModel.setDecimalDigits(field.get("DECIMAL_DIGITS") != null ? Integer.parseInt(field.get("DECIMAL_DIGITS").toString()) : null);
                // 允许为空：0 不允许 1 允许
                dbModel.setNullable(Integer.parseInt(field.get("NULLABLE").toString()) == DatabaseMetaData.columnNullable);
                dbModel.setDefaultValue(field.get("COLUMN_DEF").toString());
                dbModel.setContent(field.get("REMARKS").toString());
                list.add(dbModel);
            }
        } else {
            try (ResultSet rs = connection.getMetaData().getColumns(connection.getCatalog(), connection.getSchema(), tableName, null)) {
                while (rs.next()) {
                    FieldDbModel dbModel = new FieldDbModel();
                    dbModel.setFieldName(rs.getString("COLUMN_NAME"));
                    dbModel.setPk(dbModel.getFieldName().equals(primaryKey));
                    dbModel.setType(rs.getString("TYPE_NAME"));

                    dbModel.setLength(rs.getInt("COLUMN_SIZE"));
                    dbModel.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    // 允许为空：0 不允许 1 允许
                    dbModel.setNullable(rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    dbModel.setDefaultValue(rs.getString("COLUMN_DEF"));
                    dbModel.setContent(rs.getString("REMARKS"));
                    list.add(dbModel);
                }
            }
        }
        if (list.size() == 0) {
            throw new JobExecuteException(String.format("the table '%s' did not query the columns , please check.", tableName));
        }
        return list;
    }

    private String getPrimaryKey(String tableName, Connection connection) {
        try (ResultSet rs = connection.getMetaData().getPrimaryKeys(connection.getCatalog(), connection.getSchema(), tableName)) {
            if (rs.next()) {
                //主键列名
                return rs.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            throw new JobExecuteException(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 获取表的索引信息
     *
     * @return
     */
    private Map<String, IndexModel> getAllIndex(String tableName, Connection connection) {
        Map<String, IndexModel> map = new HashMap<>();
        try (ResultSet rs = connection.getMetaData().getIndexInfo(connection.getCatalog(), connection.getSchema(), tableName, false, false)) {
            ResultSetPrinter.printResultSet(rs);
            /*if (rs.next()) {
                //主键列名
                return rs.getString("COLUMN_NAME");
            }*/
        } catch (SQLException e) {
            throw new JobExecuteException(e.getMessage(), e);
        }
        return map;
    }

    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) {
            }
        }
    }
}
