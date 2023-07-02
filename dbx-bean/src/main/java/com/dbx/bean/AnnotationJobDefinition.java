package com.dbx.bean;

import com.dbx.bean.config.support.AnnotationDataSqlExecutor;
import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.db.sql.DataSqlExecutor;
import com.dbx.core.exception.JobDefinitionException;
import com.dbx.core.job.JobConfig;
import com.dbx.core.job.JobDefinition;
import com.dbx.core.job.JobTool;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 原始数据定义
 *
 * @author Aqoo
 */
@Slf4j
public class AnnotationJobDefinition implements JobDefinition {
    private final String id;
    private final JobConfig jobConfig;

    /**
     * MapperDefinition的id组成的数据定义
     */
    private final Map<String, TableMapperDefinition> mapperIdLink = new HashMap<>();

    private JobTool jobTool;


    public AnnotationJobDefinition(String id, JobConfig jobConfig) {
        this.id = id;
        this.jobConfig = jobConfig;
    }

    @Override
    public @NonNull JobConfig getJobConfig() {
        return jobConfig;
    }

    @Override
    public @NonNull String getJobId() {
        return id;
    }

    @Override
    public @NonNull JobTool getJobTool() {
        if (jobTool == null) {
            jobTool = new AnnotationJobTool(jobConfig);
        }
        return jobTool;
    }

    @Override
    public TableMapperDefinition getMapperDefinition(String mapperDefinitionId) {
        return mapperIdLink.get(mapperDefinitionId);
    }

    @Override
    public TableMapperDefinition[] getAllMapperDefinitions() {
        return mapperIdLink.values().toArray(new TableMapperDefinition[0]);
    }


    public void addMapperDefinition(@NonNull TableMapperDefinition mapperDefinition) {
        if (mapperIdLink.containsKey(mapperDefinition.getId())) {
            throw new JobDefinitionException(String.format("the source '%s' ddl definition is already exist,please check.",
                    mapperDefinition.getTableModel().getTableName()));
        }
        mapperIdLink.put(mapperDefinition.getId(), mapperDefinition);
    }

    static class AnnotationJobTool extends JobTool {

        public AnnotationJobTool(JobConfig jobConfig) {
            super(jobConfig);
        }

        @Override
        public DataSqlExecutor getDataSqlExecutor() {
            if(dataSqlExecutor==null){
                dataSqlExecutor = new AnnotationDataSqlExecutor(this);
            }
            return dataSqlExecutor;
        }
    }
}
