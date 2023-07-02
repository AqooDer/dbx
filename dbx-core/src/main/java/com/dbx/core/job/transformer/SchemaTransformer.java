package com.dbx.core.job.transformer;

import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.db.data.CreateTableSqLInfo;
import com.dbx.core.job.JobDefinition;
import com.dbx.core.job.JobTool;
import com.dbx.core.job.Transformer;
import com.dbx.core.script.SqlScriptWriter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Slf4j
public abstract class SchemaTransformer implements Transformer {

    private JobDefinition jobDefinition;

    public SchemaTransformer(JobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public void transfer() {
        JobTool jobTool = jobDefinition.getJobTool();
        for (TableMapperDefinition mapperDefinition : jobDefinition.getAllMapperDefinitions()) {

            CreateTableSqLInfo sqlInfo = jobTool.getTargetSqlGenerator().getCreateTableSql(mapperDefinition.getTableModel());

            if (jobDefinition.getJobConfig().enableCreateTable()) {
                if (log.isDebugEnabled()) {
                    log.debug("execute create table SQL : {}", sqlInfo.allSql());
                }
                jobTool.getDataSourceMapping().getTargetWrapper().executeInTx((jdbcTemplate) -> {
                    jdbcTemplate.update(sqlInfo.getDropSql());
                    jdbcTemplate.update(sqlInfo.getCreateSql());
                    log.info("create table {} success.", mapperDefinition.getTableModel().getTableName());
                    return true;
                });
            }

            if (jobDefinition.getJobConfig().enableCreateSchemaScript()) {
                if (log.isDebugEnabled()) {
                    log.debug("writing table sql script : {}", mapperDefinition.getTableModel().getTableName());
                }
                SqlScriptWriter.writeSchemaScript(jobDefinition.getJobId(), sqlInfo.allSql());
            }
        }
        if (jobDefinition.getJobConfig().enableCreateSchemaScript()) {
            log.info(String.format("the schema script created success, path of script file : %s ", SqlScriptWriter.getRootPath()));
        }
    }
}
