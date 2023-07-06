package com.dbx.core.db.sql.generator.impl;

import com.dbx.core.db.data.CreateTableSqLInfo;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.db.sql.generator.AbstractSqlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Aqoo
 */
@Slf4j
public class MysqlSqlGenerator extends AbstractSqlGenerator {

    @Override
    protected CreateTableSqLInfo doGenerateCreateTableSql(TableModel tableModel) {
        Set<Map.Entry<String, FieldModel>> entries = tableModel.getFieldModels().entrySet();
        String createSql = (" CREATE TABLE `%s` ( " + CR + " %s " + CR + ")  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 %s ");
        StringBuilder sb = new StringBuilder();
        String primaryKey = "";
        int i = 1;
        for (Map.Entry<String, FieldModel> entry : entries) {
            FieldDbModel dbModel = entry.getValue().getFieldDbModel();
            sb.append(String.format("`%s` ", dbModel.getFieldName().trim()));
            sb.append(dbModel.getType());
            boolean nullable = dbModel.getNullable();
            String defaultValue = dbModel.getDefaultValue();
            if (Boolean.TRUE.equals(dbModel.getPk())) {
                sb.append(" NOT NULL ");
                primaryKey = dbModel.getFieldName();
            } else {
                if (nullable) {
                    sb.append(" DEFAULT NULL ");
                } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty()) {
                    if ("NULL".equalsIgnoreCase(defaultValue)) {
                        sb.append(" DEFAULT NULL");
                    } else if (defaultValue.toUpperCase().trim().startsWith("CURRENT_TIMESTAMP")) {
                        // 处理时间字段的默认当前时间问题
                        sb.append(String.format(" ON UPDATE %s", defaultValue));
                    } else if (defaultValue.contains("0000-00-00 00:00:00")) {
                        // 兼容高版本mysql不支持  DEFAULT '0000-00-00 00:00:00' 的写法
                        sb.append(" DEFAULT NULL");
                    } else {
                        sb.append(String.format(" DEFAULT '%s'", defaultValue));
                    }
                } else {
                    sb.append(" NOT NULL");
                }
            }

            if (StringUtils.hasText(dbModel.getContent())) {
                sb.append(" COMMENT '").append(dbModel.getContent()).append("'");
            }

            if (i < entries.size()) {
                sb.append(",");
            } else {
                sb.append("  ");
            }
            i++;
            sb.append(CR);
        }
        if (!primaryKey.isEmpty()) {
            sb.append(", PRIMARY KEY (").append(primaryKey).append(")").append(CR);
        }
        String content = StringUtils.hasText(tableModel.getContent()) ? "comment= '" + tableModel.getContent() + "'" : "";
        String sql = String.format(createSql, tableModel.getTableName(), sb, content);
        String dropTableSql = getDropTableSql(tableModel);
        return CreateTableSqLInfo.builder().dropSql(dropTableSql).createSql(sql).cr(CR).build();
    }

    @Override
    protected String doGetDropTableSql(TableModel tableModel) {
        return null;
    }

    @Override
    protected String doGetInsertSql(TableModel tableModel, List<String> fields, List<String> values) {
        return null;
    }

    @Override
    public String getSegmentDataQuerySql(TableModel tableModel, int start, int length) {
        String template = "select * from {tableName} limit {start},{length};";
        return template.replace("{tableName}", tableModel.getTableName())
                .replace("{start}", String.valueOf(start)).replace("{length}", String.valueOf(length));
    }

    @Override
    public String getTableModelQuerySql(String tableName) {
        return null;
    }

    @Override
    public String getFieldModelQuerySql(String tableName) {
        return null;
    }

}
