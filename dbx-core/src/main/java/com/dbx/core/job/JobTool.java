package com.dbx.core.job;

import com.dbx.core.constans.DbType;
import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.db.sql.DataSqlExecutor;
import com.dbx.core.db.sql.SqlFactory;
import com.dbx.core.db.sql.generator.SqlGenerator;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.db.sql.resolver.FieldValueResolver;
import com.dbx.core.exception.JobException;
import lombok.NonNull;

public abstract class JobTool {
    protected DataSourceMapping dataSourceMapping;

    protected DataSqlExecutor dataSqlExecutor;

    protected FieldTypeResolver sourceFieldTypeResolver;
    protected FieldTypeResolver targetFieldTypeResolver;

    protected SqlGenerator sourceSqlGenerator;

    protected SqlGenerator targetSqlGenerator;

    protected FieldValueResolver fieldValueResolver;

    public JobTool(JobConfig jobConfig) {
        this.dataSourceMapping = new DataSourceMapping(jobConfig.dbTransferType(), jobConfig.getSourceDataSourceConfig(), jobConfig.getTargetDataSourceConfig());
    }

    @NonNull
    public DataSourceMapping getDataSourceMapping() {
        return dataSourceMapping;
    }


    /**
     * 获取 数据操作执行器
     *
     * @return 数据sql执行器
     */
    public DataSqlExecutor getDataSqlExecutor() {
        throw new JobException("Please rewrite this method.");
    }

    /**
     * 获取 源库的sql生成器
     *
     * @return 返回sql生成器
     */
    public SqlGenerator getSourceSqlGenerator() {
        DbType source = dataSourceMapping.getDbTransferType().source();
        if (sourceSqlGenerator == null && source != null) {
            sourceSqlGenerator = SqlFactory.getSqlGeneratorInstance(source);
        }
        return sourceSqlGenerator;
    }

    /**
     * 获取目标库的sql生成器
     *
     * @return 返回目标sql语句生成器
     */
    public SqlGenerator getTargetSqlGenerator() {
        DbType target = dataSourceMapping.getDbTransferType().target();
        if (targetSqlGenerator == null && target != null) {
            targetSqlGenerator = SqlFactory.getSqlGeneratorInstance(target);
        }
        return targetSqlGenerator;
    }


    /**
     * 值解析起
     *
     * @return 返回字段解析器
     */
    public FieldValueResolver getFieldValueResolver() {
        if (fieldValueResolver == null) {
            DbType source = dataSourceMapping.getDbTransferType().source();
            DbType target = dataSourceMapping.getDbTransferType().target();
            fieldValueResolver = SqlFactory.getFieldValueResolver(source, target);
        }
        return fieldValueResolver;
    }


    public FieldTypeResolver getSourceFieldTypeResolver() {
        if (sourceFieldTypeResolver == null) {
            DbType type = dataSourceMapping.getSourceWrapper().getDbType();
            sourceFieldTypeResolver = SqlFactory.getFieldTypeResolverInstance(type);
            sourceFieldTypeResolver.setDataSourceMapping(dataSourceMapping);
        }
        return sourceFieldTypeResolver;
    }

    public FieldTypeResolver getTargetFieldTypeResolver() {
        if (targetFieldTypeResolver == null) {
            DbType type = dataSourceMapping.getDbTransferType().target();
            targetFieldTypeResolver = SqlFactory.getFieldTypeResolverInstance(type);
            targetFieldTypeResolver.setDataSourceMapping(dataSourceMapping);
        }
        return targetFieldTypeResolver;
    }

}
