package com.dbx.core.job;

import com.dbx.core.config.TableMapperDefinition;
import lombok.NonNull;

/**
 *
 */
public interface JobDefinition {

    /**
     * 获取一个job定义的id
     *
     * @return id
     */
    @NonNull String getJobId();


    @NonNull JobConfig getJobConfig();


    @NonNull JobTool getJobTool();

    /**
     * 从容器中获取一个MapperDefinition
     *
     * @param mapperDefinitionId 定义id
     * @return MapperDefinition
     */
    TableMapperDefinition getMapperDefinition(String mapperDefinitionId);

    /**
     * 获取所有的MapperDefinition
     *
     * @return MapperDefinition
     */
    TableMapperDefinition[] getAllMapperDefinitions();

}
