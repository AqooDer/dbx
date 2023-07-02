package com.dbx.core.config;

import com.dbx.core.exception.JobDefinitionException;

/**
 * @author Aqoo
 */
public interface FieldValueFormatDefinition {
    /**
     * 获取当前 值 定义格式
     *
     * @return 返回一个当前格式化值的定义
     * @throws JobDefinitionException 定义异常
     */
    Object getValueFormat() throws JobDefinitionException;

}
