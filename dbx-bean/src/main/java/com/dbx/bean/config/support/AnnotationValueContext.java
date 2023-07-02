package com.dbx.bean.config.support;

import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.db.data.ValueContext;
import com.dbx.core.db.data.RowValueState;
import com.dbx.core.db.datasource.DataSourceWrapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * @author Aqoo
 */
@Data
@AllArgsConstructor
@Builder
public class AnnotationValueContext implements ValueContext {

    TableRowValueMapperDefinition tableRowValueMapperDefinition;

    Map<String, Object> sourceValue;
    /**
     * 注意；运行时中，解析一个赋值一个
     * 动态赋值
     */
    Map<String, Object> targetValue;

    DataSourceMapping dataSourceMapping;

    /**
     * 父级 值上下文
     */
    ValueContext parent;

    private Map<String, Map<String, Object>> otherValues;

    private final RowValueState valueState;

    @Override
    public RowValueState getValueState() {
        return valueState;
    }

    @Override
    public TableRowValueMapperDefinition getTableRowValueMapperDefinition() {
        return tableRowValueMapperDefinition;
    }

    @Override
    public DataSourceWrapper getSourceWrapper() {
        return dataSourceMapping.getSourceWrapper();
    }

    @Override
    public DataSourceWrapper getTargetWrapper() {
        return dataSourceMapping.getTargetWrapper();
    }

    @Override
    public Map<String, Map<String, Object>> getOtherValues() {
        return otherValues;
    }

    @Override
    public void putOtherValues(String key, Map<String, Object> data) {
        otherValues.put(key, data);
    }
}
