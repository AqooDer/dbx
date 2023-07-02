package com.dbx.core.job.transformer;

import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.TableValueExecStatus;
import com.dbx.core.db.sql.DataSqlExecutor;
import com.dbx.core.db.sql.DataSqlObject;
import com.dbx.core.job.JobDefinition;
import com.dbx.core.job.JobTool;
import com.dbx.core.job.Transformer;
import com.dbx.core.script.SqlScriptWriter;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class DataTransformer implements Transformer {

    protected final Set<String> deletedTables = new HashSet<>();

    private final JobDefinition jobDefinition;

    public DataTransformer(JobDefinition jobDefinition) {
        this.jobDefinition = jobDefinition;
    }

    /**
     * 输出表数据到 目标数据表或sql脚本中
     */
    @Override
    public void transfer() {
        for (TableMapperDefinition mapperDefinition : jobDefinition.getAllMapperDefinitions()) {
            Map<String, TableRowValueMapperDefinition> fieldMapperDefinitions = mapperDefinition.getFieldMapperDefinitions();
            // 派发表数据插入事件
            clearTable(mapperDefinition);
            DataSqlExecutor dataSqlExecutor = jobDefinition.getJobTool().getDataSqlExecutor();
            fieldMapperDefinitions.forEach((k, tvm) -> {
                int counter = 1;
                try {
                    do {
                        DataSqlObject dataSqlObject = dataSqlExecutor.generatorInsertSql(tvm, counter);
                        if (dataSqlObject.getBatchArgs().size() != 0) {
                            if (jobDefinition.getJobConfig().enableInsertData()) {
                                dataSqlExecutor.batchSaveData(dataSqlObject);
                            }

                            if (jobDefinition.getJobConfig().enableCreateDataScript()) {
                                List<String> writeSqlList = dataSqlObject.getWriteSqls();
                                if (dataSqlObject.getChildren() != null && !dataSqlObject.getChildren().isEmpty()) {
                                    dataSqlObject.getChildren().forEach((tvmId, v) -> writeSqlList.addAll(v.getWriteSqls()));
                                }
                                SqlScriptWriter.writeDataScript(jobDefinition.getJobConfig().mergeDataScript(),
                                        jobDefinition.getJobId(), mapperDefinition.getId(), writeSqlList.toArray(new String[0]));
                            }
                            counter++;
                        }
                    } while (tvm.getTableValueExecStatus() == TableValueExecStatus.EXECUTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        if (jobDefinition.getJobConfig().enableCreateDataScript()) {
            log.info(String.format("the data script created success, path of script file : %s ", SqlScriptWriter.getRootPath()));
        }
    }

    /**
     * 在写入数据之前，请空表的数据、
     *
     * @param mapperDefinition 数据表定义的所有信息
     */
    private void clearTable(TableMapperDefinition mapperDefinition) {
        Set<String> tableInsert = new HashSet<>();
        tableInsert.add(mapperDefinition.getTableModel().getTableName());
        // 子类处理
        mapperDefinition.getFieldMapperDefinitions().forEach((k, v) -> tableInsert.addAll(Arrays.stream(Optional.ofNullable(v.child()).orElse(new TableRowValueMapperDefinition[0]))
                .map(tvm -> tvm.getTableMapperDefinition().getTableModel().getTableName()).collect(Collectors.toSet())));
        Sets.SetView<String> difference = Sets.difference(tableInsert, deletedTables);

        JobTool jobTool = jobDefinition.getJobTool();
        if (jobDefinition.getJobConfig().enableClearTableDataBeforeInsert() && difference.size() > 0) {
            List<String> sqlList = new ArrayList<>();
            for (String goalTable : difference) {
                sqlList.add(jobTool.getTargetSqlGenerator().getClearTableSql(goalTable));
            }
            if (jobDefinition.getJobTool().getDataSourceMapping().getTargetWrapper() == null) {
                log.warn(" the target datasource is not defined . please check. ");
            } else {
                jobTool.getDataSqlExecutor().batchClearTables(sqlList.toArray(new String[0]));
                log.info(" clear table {} data before insert success.", difference.toString());
            }
        }
        // addAll之后，difference大小为0；
        deletedTables.addAll(difference);
    }
}
