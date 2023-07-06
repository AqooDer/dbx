package com.dbx.bean.config.support;


import com.dbx.bean.config.resolve.definition.AnnotationTableRowValueMapperDefinition;
import com.dbx.bean.config.resolve.definition.AnnotationValueFormatDefinition;
import com.dbx.bean.util.ValueFormatUtil;
import com.dbx.core.config.FieldValueFormatDefinition;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.TableValueExecStatus;
import com.dbx.core.constans.ValueExecState;
import com.dbx.core.db.data.RowValueState;
import com.dbx.core.db.data.ValueContext;
import com.dbx.core.db.data.ValueFormat;
import com.dbx.core.db.datasource.DataSourceWrapper;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.sql.DataSqlExecutor;
import com.dbx.core.db.sql.DataSqlObject;
import com.dbx.core.db.sql.generator.SqlGenerator;
import com.dbx.core.exception.JobException;
import com.dbx.core.exception.JobExecuteException;
import com.dbx.core.job.JobTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Aqoo
 */
@Slf4j
public class AnnotationDataSqlExecutor implements DataSqlExecutor {

    private final JobTool jobTool;

    private final int batchSqlCount;

    public AnnotationDataSqlExecutor(JobTool jobTool) throws JobException {
        this(jobTool, BATCH_SQL_COUNT);
    }

    public AnnotationDataSqlExecutor(JobTool jobTool, int batchSqlCount) throws JobException {
        this.jobTool = jobTool;
        this.batchSqlCount = batchSqlCount;
    }

    @Override
    public DataSqlObject generatorInsertSql(final TableRowValueMapperDefinition tvm, final int counter) throws JobException {
        DataSqlObject dataSqlObject = new DataSqlObject();
        // 当源表存在时，如果没有数据，那么该值也就没有处理的必要了
        AnnotationTableRowValueMapperDefinition atm = (AnnotationTableRowValueMapperDefinition) tvm;
        if (tvm.getSourceValueTableModel() != null) {
            List<Map<String, Object>> sourceData = new ArrayList<>(querySegmentDataFromSource(tvm, counter, batchSqlCount));
            if (!sourceData.isEmpty()) {
                atm.setTableValueExecStatus(TableValueExecStatus.EXECUTING);
                for (Map<String, Object> sourceDatum : sourceData) {
                    createSingleGroupSql(dataSqlObject, tvm, sourceDatum, null);
                }
            } else {
                atm.setTableValueExecStatus(TableValueExecStatus.COMPLETED);
            }
        } else {
            // 处理没有原表没有值的情况 1.没有原表，该表是新创建的非动态表，比如枚举表等。
            atm.setTableValueExecStatus(TableValueExecStatus.EXECUTING);
            createSingleGroupSql(dataSqlObject, tvm, new HashMap<>(8), null);
            atm.setTableValueExecStatus(TableValueExecStatus.COMPLETED);
        }
        return dataSqlObject;
    }


    /**
     * 生成主表的sql语句以及相关子表的sql语句
     *
     * @param tvm                表定义信息
     * @param sourceDatum        单行数据
     * @param parentValueContext 父表数据上下文信息
     * @return 主表的sql语句和子表的sql语句
     */
    private void createSingleGroupSql(DataSqlObject dataSqlObject, final TableRowValueMapperDefinition tvm, final Map<String, Object> sourceDatum,
                                      final ValueContext parentValueContext) {
        Map<String, Object> objectMap = new HashMap<>(32);
        AnnotationValueContext thisValueContext = AnnotationValueContext.builder().sourceValue(sourceDatum).targetValue(objectMap).tableRowValueMapperDefinition(tvm).
                dataSourceMapping(jobTool.getDataSourceMapping()).valueState(new RowValueState()).
                parent(parentValueContext).otherValues(new HashMap<>(8)).build();

        boolean flag = tvm.getValueFormat() != null;
        if (flag) {
            tvm.getValueFormat().prepare(thisValueContext);
        }
        if (thisValueContext.getValueState().getValueExecState().equals(ValueExecState.NONE)) {
            return;
        }

        // 1.创建 insertSql 模版
        List<String> fields = new ArrayList<>();
        List<String> writeValues = new ArrayList<>();
        List<Object> execValues = new ArrayList<>();
        boolean addSql = true;
        for (TableFieldValueMapperDefinition fvm : tvm.getFieldValueMapperDefinition()) {
            // 获取原始数据的值
            Object initValue = getValue(tvm.getId(), fvm, thisValueContext, tvm.getValueFormat());
            FieldDbModel fieldDbModel = fvm.getTargetFieldModel().getFieldDbModel();
            // 根据ddl定义，进行数据的转换
            String writeValue = jobTool.getFieldValueResolver()
                    .writeResolve(jobTool.getDataSourceMapping().getDbTransferType(), tvm, fvm, initValue);

            Object execValue = jobTool.getFieldValueResolver()
                    .execResolve(jobTool.getDataSourceMapping().getDbTransferType(), tvm, fvm, initValue);

            // 判断该字段是否不能为null ，但是实际却是null (包含主键的检测)
            if (!fieldDbModel.getNullable() && !StringUtils.hasText(writeValue)) {
                if (parentValueContext != null) {
                    log.error(" data cannot be generated because the field value cannot be empty, but the actual value is empty . please check the parent table value definition " +
                                    "id is '{}'" +
                                    " , the table value definition id is '{}', the field definition is '{}'",
                            parentValueContext.getTableRowValueMapperDefinition().getId(), tvm.getId(), fvm.getTargetFieldModel().getFieldDbModel());
                } else {
                    log.error(" data cannot be generated because the field value cannot be empty, but the actual value is empty .  please check the table value definition id is " +
                                    "'{}', the field definition is '{}'",
                            tvm.getId(), fvm.getTargetFieldModel().getFieldDbModel());
                }
                addSql = false;
            } else {
                execValues.add(execValue);
                objectMap.put(fvm.getTargetField(), execValue);
                fields.add(fvm.getTargetField());
                writeValues.add(writeValue);
            }
        }

        if (tvm.child() != null) {
            // 如果主表没得数据不能得到安全的处理，那么子表该数据就不处理。
            if (!addSql) {
                log.warn(" cannot process sub table data because the main table has no data , please check the main table definition id is '{}',sub tables definition id is '{}'",
                        tvm.getTableMapperDefinition().getId(), Arrays.stream(tvm.child()).map(child -> child.getTableMapperDefinition().getId()).collect(Collectors.joining(",")));
            } else {
                for (TableRowValueMapperDefinition child : tvm.child()) {
                    DataSqlObject childDataSqlObject = dataSqlObject.getChildren().get(child.getId());
                    childDataSqlObject = childDataSqlObject == null ? new DataSqlObject() : childDataSqlObject;
                    createSingleGroupSql(childDataSqlObject, child, new HashMap<>(8), thisValueContext);
                    dataSqlObject.getChildren().put(child.getId(), childDataSqlObject);
                }
            }
        }

        if (thisValueContext.getValueState().getValueExecState().equals(ValueExecState.CREATE_VALUE)) {
            return;
        }

        // 映射字段，根据字段排序生成值，
        SqlGenerator targetSqlGenerator = jobTool.getTargetSqlGenerator();
        String sql = targetSqlGenerator.getWriteInsertSql(tvm.getTableMapperDefinition().getTableModel(), fields, writeValues);
        if (addSql) {
            if (dataSqlObject.getExecSql() == null) {
                dataSqlObject.setExecSql(targetSqlGenerator.getExecInsertSql(tvm.getTableMapperDefinition().getTableModel(), fields));
            }
            dataSqlObject.getWriteSqls().add(sql);
            dataSqlObject.getBatchArgs().add(execValues.toArray(new Object[0]));
        }
        if (flag) {
            tvm.getValueFormat().end(thisValueContext);
        }
    }


    private Object getValue(String id, TableFieldValueMapperDefinition fvm, ValueContext valueContext, ValueFormat valueFormat) {
        Object value = null;
        if (valueFormat != null) {
            value = valueFormat.format(fvm, valueContext);
            if (value != null) {
                return value;
            }
        }
        for (FieldValueFormatDefinition fieldValueFormatDefinition : fvm.getValueFormatDefinition()) {
            AnnotationValueFormatDefinition avf = (AnnotationValueFormatDefinition) fieldValueFormatDefinition;
            int type = avf.getType();
            if (value != null) {
                return value;
            }

            if (type == 2) {
                value = ValueFormatUtil.valueFormat((ValueDefaultType) avf.getValueFormat());
            }
            if (type == 3) {
                value = avf.getValueFormat();
            }
            if (type == 4) {
                ValueContext parent = valueContext.getParent();
                if (parent == null) {
                    throw new JobExecuteException(String.format("format value error , the  parent data is null of '%s  %s' definition .", id, fvm.getTargetField()));
                }
                value = parent.getTargetValue().get(avf.getValueFormat().toString());
            }
            if (type == 5) {
                ValueContext parent = valueContext.getParent();
                if (parent == null) {
                    throw new JobExecuteException(String.format("format value error , the  parent data is null of '%s  %s' definition .", id, fvm.getTargetField()));
                }
                value = parent.getSourceValue().get(avf.getValueFormat().toString());
            }
            if (type == 6) {
                // 处理child类时 valueContext.getSourceValue() 为空
                if (valueContext.getSourceValue() == null) {
                    return null;
                }
                value = valueContext.getSourceValue().get(avf.getValueFormat().toString());
            }
        }
        return value;
    }

    @Override
    public void batchSaveData(DataSqlObject dataSqlObject) {
        DataSourceWrapper targetWrapper = jobTool.getDataSourceMapping().getTargetWrapper();
        if (targetWrapper != null) {
            targetWrapper.executeInTx(jdbcTemplate -> {
                int[] no = jdbcTemplate.batchUpdate(dataSqlObject.getExecSql(), dataSqlObject.getBatchArgs());
                if (no.length != dataSqlObject.getBatchArgs().size()) {
                    throw new JobExecuteException("sql execute error");
                }
                Map<String, DataSqlObject> children = dataSqlObject.getChildren();
                if (children != null && !children.isEmpty()) {
                    for (DataSqlObject child : children.values()) {
                        no = jdbcTemplate.batchUpdate(child.getExecSql(), child.getBatchArgs());
                        if (no.length != child.getBatchArgs().size()) {
                            throw new JobExecuteException("sql execute error");
                        }
                    }
                }
                return no;
            });
        }
    }

    @Override
    public void batchClearTables(String[] sqlList) {
        DataSourceWrapper targetWrapper = jobTool.getDataSourceMapping().getTargetWrapper();
        if (targetWrapper != null) {
            targetWrapper.executeInTx(jdbcTemplate -> {
                int[] no = jdbcTemplate.batchUpdate(sqlList);
                if (no.length != sqlList.length) {
                    throw new JobExecuteException("sql execute error");
                }
                return no;
            });
        }
    }


    @Override
    public List<Map<String, Object>> querySegmentDataFromSource(final TableRowValueMapperDefinition tvm, int num, int length) throws JobException {
        int start = num <= 1 ? 0 : (num - 1) * length;
        String querySql = jobTool.getSourceSqlGenerator().getSegmentDataQuerySql(tvm.getSourceValueTableModel(), start, length);
        return jobTool.getDataSourceMapping().getSourceWrapper().execute(jdbcTemplate -> jdbcTemplate.queryForList(querySql));
    }
}