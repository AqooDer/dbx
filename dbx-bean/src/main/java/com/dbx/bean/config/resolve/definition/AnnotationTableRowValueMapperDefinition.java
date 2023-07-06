package com.dbx.bean.config.resolve.definition;

import com.dbx.bean.config.resolve.Meson;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.TableValueExecStatus;
import com.dbx.core.db.data.ValueFormat;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDefinitionException;
import lombok.ToString;

import java.util.List;

/**
 * @author Aqoo
 */
@ToString()
public class AnnotationTableRowValueMapperDefinition implements TableRowValueMapperDefinition {

    private final String id;

    private final TableMapperDefinition mapperDefinition;

    private TableRowValueMapperDefinition[] child;

    private List<TableFieldValueMapperDefinition> fieldValueMapperDefinition;

    private ValueFormat valueFormat;

    private final TableModel sourceValueTableModel;

    private TableValueExecStatus status = TableValueExecStatus.PENDING;

    public AnnotationTableRowValueMapperDefinition(Meson meson, TableMapperDefinition mapperDefinition) {
        this.id = meson.getConfig().getName();
        this.mapperDefinition = mapperDefinition;
        this.sourceValueTableModel = meson.getSourceTableModel();
        MapperTable mapperTable = meson.getMapperTable();
        if (!mapperTable.getClass().isAssignableFrom(ValueFormat.class)) {
            try {
                this.valueFormat = mapperTable.customFormatValue().newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new JobDefinitionException("instance value format class fail.");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                throw new JobDefinitionException("instance value format class fail. Please check whether the class is public.");
            }
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TableMapperDefinition getTableMapperDefinition() {
        return mapperDefinition;
    }

    @Override
    public TableModel getSourceValueTableModel() {
        return sourceValueTableModel;
    }

    @Override
    public TableRowValueMapperDefinition[] child() {
        return child;
    }

    @Override
    public ValueFormat getValueFormat() {
        return valueFormat;
    }

    @Override
    public TableValueExecStatus getTableValueExecStatus() {
        return status;
    }

    @Override
    public List<TableFieldValueMapperDefinition> getFieldValueMapperDefinition() {
        return fieldValueMapperDefinition;
    }

    public void setTableValueExecStatus(TableValueExecStatus status) {
        this.status = status;
    }

    public void setChild(TableRowValueMapperDefinition[] child) {
        this.child = child;
    }

    public void setFieldValueMapperDefinition(List<TableFieldValueMapperDefinition> fieldValueMapperDefinition) {
        this.fieldValueMapperDefinition = fieldValueMapperDefinition;
    }
}
