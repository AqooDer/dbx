package com.dbx.core.job.transformer;

import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.db.datasource.DataSourceWrapper;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobException;
import com.dbx.core.job.*;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
public class ComparisonSchemaTransformer implements Transformer {

    private final JobDefinition jobDefinition;

    private static final String CR = Arrays.toString(System.lineSeparator().getBytes(StandardCharsets.UTF_8));

    /**
     * 目标数据库没有的字段，但是定义上面有点字段
     */
    private final StringBuilder redundant = new StringBuilder();

    /**
     * 目标数据库有的字段，但是定义上没有的字段
     */
    private final StringBuilder undefined = new StringBuilder();

    private final Map<String, Set<String>> tableFieldMap = new HashMap<>();

    private boolean transfer = false;

    public ComparisonSchemaTransformer(JobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    @Override
    public void transfer() {
        write(transferTo(read()));
    }

    /**
     * 读取原始数据库的原始字段列表
     *
     * @return
     */
    public Map<String, Set<String>> read() {
        if (!tableFieldMap.isEmpty()) {
            return tableFieldMap;
        }
        JobTool jobTool = jobDefinition.getJobTool();
        DataSourceWrapper targetWrapper = jobDefinition.getJobTool().getDataSourceMapping().getTargetWrapper();
        for (TableMapperDefinition tmd : jobDefinition.getAllMapperDefinitions()) {
            TableModel tableModel = tmd.getTableModel();
            List<FieldDbModel> fieldDbModel = targetWrapper.getFieldDbModel(tableModel.getTableName(), jobTool.getTargetSqlGenerator());
            Set<String> existFieldNames = fieldDbModel.stream().map(FieldDbModel::getFieldName).collect(Collectors.toSet());
            tableFieldMap.put(tableModel.getTableName(), existFieldNames);
        }
        return tableFieldMap;
    }


    public String transferTo(Map<String, Set<String>> read) {
        Supplier<String> supplier = () -> {
            if (redundant.length() > 0 || undefined.length() > 0) {
                return redundant.append(CR).append(undefined).toString();
            }
            return null;
        };
        if (transfer) {
            supplier.get();
        }
        for (TableMapperDefinition tmd : jobDefinition.getAllMapperDefinitions()) {
            Set<String> thisFieldNames = tmd.getTableModel().getFieldModels().keySet();
            Set<String> targetDbFields = read.get(tmd.getTableModel().getTableName());

            Sets.SetView<String> difference = Sets.difference(thisFieldNames, targetDbFields);
            if (!difference.isEmpty()) {
                redundant.append(String.format("the table %s definition error , this fields %s is redundant", tmd.getTableModel().getTableName(), difference)).append(CR);
            }

            difference = Sets.difference(targetDbFields, thisFieldNames);
            if (!difference.isEmpty()) {
                undefined.append(String.format(String.format("the table %s definition error , this fields %s is undefined", tmd.getTableModel().getTableName(), difference))).append(CR);
            }
        }
        transfer = true;
        return supplier.get();
    }


    /**
     * 输出错误到控制台
     */
    public void write(String result) {
        if (StringUtils.hasText(result)) {
            log.error("Detection mapping error:{}", result);
            throw new JobException("Detection mapping error.");
        }
    }


}
