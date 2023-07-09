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
 * CREATE TABLE "public"."re_client_fill_data" (
 * "id" varchar(64) COLLATE "pg_catalog"."default" NOT NULL,
 * "crop_code" varchar(64) COLLATE "pg_catalog"."default",
 * "crop_name" varchar(500) COLLATE "pg_catalog"."default",
 * "data_status" int4,
 * "data_report_record_id" varchar(64) COLLATE "pg_catalog"."default",
 * "res_info_id" varchar(64) COLLATE "pg_catalog"."default",
 * "line_flag" int4,
 * "res_info_name" varchar(256) COLLATE "pg_catalog"."default",
 * "create_date" timestamp(6),
 * "data_type" varchar(64) COLLATE "pg_catalog"."default",
 * "index_id" varchar(64) COLLATE "pg_catalog"."default",
 * "index_name" varchar(256) COLLATE "pg_catalog"."default",
 * "modify_date" timestamp(6),
 * "data_count" varchar(500) COLLATE "pg_catalog"."default",
 * "source_id" varchar(64) COLLATE "pg_catalog"."default"
 * );
 * ALTER TABLE "public"."re_client_fill_data" OWNER TO "postgres";
 * COMMENT ON COLUMN "public"."re_client_fill_data"."crop_code" IS '上报单位编码';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."crop_name" IS '上报单位名称';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."data_status" IS '数据状态 0：原有 1:新增,2：修改,3：删除';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."data_report_record_id" IS '客户端任务分发上报记录表id';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."res_info_id" IS '信息资源id';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."line_flag" IS '数据行标识，标识数据属于哪一行';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."res_info_name" IS '信息资源';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."create_date" IS '创建时间';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."data_type" IS '数据类型，Y：正式数据  ， N：测试数据';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."index_id" IS '指标id';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."index_name" IS '指标名称';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."modify_date" IS '修改时间';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."data_count" IS '值';
 * COMMENT ON COLUMN "public"."re_client_fill_data"."source_id" IS '数据源id';
 * <p>
 * ALTER TABLE "public"."re_client_fill_data" ADD CONSTRAINT "re_client_fill_data_pkey" PRIMARY KEY ("id");
 *
 * @author Aqoo
 */
@Slf4j
public class PostgreSqlGenerator extends AbstractSqlGenerator {

    /**
     * CREATE TABLE student (
     * ID INTEGER NOT NULL,
     * sex CHARACTER ( 1 ),
     * NAME CHARACTER ( 100 ),
     * PRIMARY KEY ( ID )
     * );
     *
     * @param tableModel 表定义模型
     * @return
     */
    @Override
    protected CreateTableSqLInfo doGenerateCreateTableSql(TableModel tableModel) {
        Set<Map.Entry<String, FieldModel>> entries = tableModel.getFieldModels().entrySet();
        String createSql = (" CREATE TABLE %s ( " + CR + " %s " + CR + "); ");
        String comment = " COMMENT ON COLUMN %s.%s IS '%s'; ";
        StringBuilder sb = new StringBuilder();
        StringBuilder commentSql = new StringBuilder();
        String primaryKey = "";
        int i = 1;
        if (StringUtils.hasText(tableModel.getContent())) {
            commentSql.append(" COMMENT ON TABLE ").append(tableModel.getTableName()).append(String.format(" IS '%s';", tableModel.getContent()));
            commentSql.append(CR);
        }
        for (Map.Entry<String, FieldModel> entry : entries) {
            FieldDbModel dbModel = entry.getValue().getFieldDbModel();
            sb.append(String.format(" %s ", dbModel.getFieldName().trim()));
            sb.append(dbModel.getType());
            boolean nullable = dbModel.getNullable();
            String defaultValue = dbModel.getDefaultValue();
            if (Boolean.TRUE.equals(dbModel.getPk())) {
                sb.append(" NOT NULL ");
                primaryKey = dbModel.getFieldName();
            } else {
                if (nullable) {
                    sb.append(DEFAULT_NULL_SQL_SEGMENT);
                } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty()) {
                    if (defaultValue.equalsIgnoreCase("NULL")) {
                        sb.append(DEFAULT_NULL_SQL_SEGMENT);
                    } else if ("now()".equalsIgnoreCase(defaultValue)) {
                        sb.append(" DEFAULT now() ");
                    } else {
                        sb.append(DEFAULT_NULL_SQL_SEGMENT);
                    }
                } else {
                    sb.append(" NOT NULL");
                }
            }
            if (StringUtils.hasText(dbModel.getContent())) {
                commentSql.append(String.format(comment, tableModel.getTableName(), dbModel.getFieldName(), dbModel.getContent()));
                commentSql.append(CR);
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
            sb.append(", PRIMARY KEY (").append(primaryKey).append(")");
        }
        String sql = String.format(createSql, tableModel.getTableName(), sb);
        String dropTableSql = getDropTableSql(tableModel);
        log.debug("create sql success: {}", sql + commentSql);
        return CreateTableSqLInfo.builder().dropSql(dropTableSql).createSql(sql + commentSql).cr(CR).build();
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
        String template = "select * from {tableName} limit {length} offset {start}; ";
        return template.replace("{tableName}", tableModel.getTableName())
                .replace("{length}", String.valueOf(length)).replace("{start}", String.valueOf(start));
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
