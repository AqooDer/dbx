package com.dbx.bean.config.resolve.definition;

import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.resolve.Meson;
import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.job.JobDefinition;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Aqoo
 */
@Setter
@ToString()
public class AnnotationTableMapperDefinition implements TableMapperDefinition {
    /**
     * value config包含 ddlConfig
     */
    @NonNull
    private final Set<Class<?>> valueConfigs;

    /**
     * 同一个表的数据来自于不同的源表 ，可能配置在不同的 MapperConfig 中。
     * 但是数据库链接只能使用一个，多个报错
     */
    private final MapperConfig mapperConfig;

    //private final MapperProperties mapperProperties;

    private JobDefinition jobDefinition;

    private TableModel tableModel;

    private final Map<String, String> sourceTargetFieldNameMap;


    /**
     * 使用map的原因是处理 单表数据来源不一样的问题。 一对多这种情况
     * key : class的类名
     */
    private final Map<String, TableRowValueMapperDefinition> fieldMapperDefinitions;

    private Meson meson;

    public AnnotationTableMapperDefinition(@NonNull Meson meson, @NonNull Set<Class<?>> valueConfigs, JobDefinition jobDefinition) {
        this.meson = meson;
        this.mapperConfig = meson.getMapperConfig();
        this.valueConfigs = valueConfigs;
        /*JobConfig mpc = mapperConfig.();
        boolean disableExportSql = null != mpc.disableExportSql() && mpc.disableExportSql();
        boolean isMergeDataSqlInLog = null != mpc.isMergeDataSqlInLog() && mpc.isMergeDataSqlInLog();
        boolean disableCreateTable = null != mpc.disableCreateTable() && mpc.disableCreateTable();
        boolean disableTargetTableDdlVerify = null == mpc.disableTargetTableDdlVerify() || mpc.disableTargetTableDdlVerify();
        this.valueConfigs = valueConfigs;
        this.mapperProperties =
                MapperProperties.builder()
                        .exportSql(!disableExportSql)
                        .mergeDataSql(isMergeDataSqlInLog)
                        .createTable(!disableCreateTable)
                        .comparisonDdl(!disableTargetTableDdlVerify).build();*/

        this.fieldMapperDefinitions = new HashMap<>();
        this.jobDefinition = jobDefinition;
        this.sourceTargetFieldNameMap = new HashMap<>();
    }

    @Override
    public @NonNull DataSourceMapping getDataSourceMapping() {
        return jobDefinition.getJobTool().getDataSourceMapping();
    }

    @Override
    public @NonNull String getId() {
        return meson.getMapperDefinitionId();
    }


    @Override
    public TableModel getTableModel() {
        return tableModel;
    }

    @Override
    public TableModel getSourceTableModel() {
        return meson.getSourceTableModel();
    }

    @Override
    public @NonNull Map<String, TableRowValueMapperDefinition> getFieldMapperDefinitions() {
        return fieldMapperDefinitions;
    }


    public void addTableValueMapperDefinition(Class<?> valueConfig, TableRowValueMapperDefinition tableValueMapperDefinition) {
        fieldMapperDefinitions.put(valueConfig.getName(), tableValueMapperDefinition);
    }


    public void setTableModel(TableModel tableModel) {
        this.tableModel = tableModel;
        for (FieldModel value : tableModel.getFieldModels().values()) {
            if (value.getSource() != null) {
                sourceTargetFieldNameMap.put(value.getSource().getFieldDbModel().getFieldName(), value.getFieldDbModel().getFieldName());
            }
        }
    }

    public Map<String, String> getSourceTargetFieldNameMap() {
        return sourceTargetFieldNameMap;
    }

    public MapperConfig getMapperConfig() {
        return mapperConfig;
    }

    public @NonNull Class<?> getDdlConfig() {
        return meson.getConfig();
    }

    public Set<Class<?>> getValueConfig() {
        return valueConfigs;
    }

    public String getParentId() {
        return meson.getParentMapperDefinitionId();
    }

    public Meson getMeson() {
        return meson;
    }


}
