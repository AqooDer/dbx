package com.dbx.bean.util;

import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.exception.JobDefinitionException;
import org.springframework.util.StringUtils;

/**
 * @author Aqoo
 */
public class MapperUtil {
    public static String getMapperDefinitionId(String jobId, MapperTable mapperTable) {
        return getMapperDefinitionId(jobId, mapperTable.target());
    }

    public static String getMapperDefinitionId(String jobId, String target) {
        if (!StringUtils.hasText(target)) {
            throw new JobDefinitionException("targetTableName is null ,Please assign a value first");
        }
        return jobId + '_' + target;
    }
}
