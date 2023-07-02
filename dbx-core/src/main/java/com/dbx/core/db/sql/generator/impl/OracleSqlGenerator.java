package com.dbx.core.db.sql.generator.impl;

import com.dbx.core.util.RegexUtil;
import com.dbx.core.db.data.CreateTableSqLInfo;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.db.sql.generator.AbstractSqlGenerator;
import com.dbx.core.exception.JobDefinitionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * -- 创建表: student_info 属主: scott (默认当前用户)
 * <p>
 * create table scott.student_info (
 * sno         number(10)  primary key,
 * sname       varchar2(10),
 * sex         varchar2(2),
 * create_date date
 * );
 * <p>
 * -- 添加注释
 * comment on table scott.student_info is '学生信息表';
 * comment on column scott.student_info.sno is '学号';
 * comment on column scott.student_info.sname is '姓名';
 * comment on column scott.student_info.sex is '性别';
 * comment on column scott.student_info.create_date is '创建日期';
 * <p>
 * -- 语句授权，如：给 hr 用户下列权限
 * grant select, insert, update, delete on scott.student_info to hr;
 * <p>
 * <p>
 * <p>
 * SELECT * FROM
 * (
 * SELECT A.*, ROWNUM RN
 * FROM ( SELECT * FROM ARCHIVES_AUDIT WHERE 1 = 1 ) A
 * WHERE ROWNUM <= 40
 * )
 * WHERE RN > 30
 *
 * @author Aqoo
 */
@Slf4j
public class OracleSqlGenerator extends AbstractSqlGenerator {

    public static final String ORACLE_DROP_TABLE_SQL = "declare\n" +
            "      num number;\n" +
            "begin\n" +
            "    select count(1) into num from user_tables where table_name = upper('%s') ;\n" +
            "    if num > 0 then\n" +
            "        execute immediate 'drop table %s' ;\n" +
            "    end if;\n" +
            "end;\n" +
            "/";


    @Override
    protected CreateTableSqLInfo doGenerateCreateTableSql(TableModel tableModel) {
        // 验证表名称是否附合oracle规则
        checkTableName(tableModel);

        Set<Map.Entry<String, FieldModel>> entries = tableModel.getFieldModels().entrySet();
        String createSql = (" CREATE TABLE %s ( " + cr + " %s " + cr + "); ");
        String comment = " COMMENT ON COLUMN %s.%s IS '%s'; ";
        StringBuilder sb = new StringBuilder();
        StringBuilder commentSql = new StringBuilder();
        int i = 1;
        if (StringUtils.hasText(tableModel.getContent())) {
            commentSql.append(" COMMENT ON TABLE ").append(tableModel.getTableName()).append(String.format(" IS '%s';", tableModel.getContent()));
            commentSql.append(cr);
        }
        for (Map.Entry<String, FieldModel> entry : entries) {
            FieldDbModel dbModel = entry.getValue().getFieldDbModel();
            sb.append(String.format(" %s ", dbModel.getFieldName().trim()));
            sb.append(dbModel.getType());
            boolean nullable = dbModel.getNullable();
            String defaultValue = dbModel.getDefaultValue();
            if (dbModel.getPk()) {
                sb.append(" primary key ");
            } else {
                if (nullable) {
                    sb.append(" DEFAULT NULL");
                } else if (Objects.nonNull(defaultValue) && !defaultValue.isEmpty()) {
                    if ("NULL".equalsIgnoreCase(defaultValue)) {
                        sb.append(" DEFAULT NULL");
                    } else {
                        sb.append(" DEFAULT NULL");
                    }
                } else {
                    sb.append(" NOT NULL");
                }
            }
            if (StringUtils.hasText(dbModel.getContent())) {
                commentSql.append(String.format(comment, tableModel.getTableName(), dbModel.getFieldName(), dbModel.getContent()));
                commentSql.append(cr);
            }

            if (i < entries.size()) {
                sb.append(",");
            } else {
                sb.append("  ");
            }
            i++;
            sb.append(cr);
        }
        String sql = String.format(createSql, tableModel.getTableName(), sb);
        String dropTableSql = getDropTableSql(tableModel);
        log.debug("create sql success: {}", sql + commentSql);
        return CreateTableSqLInfo.builder().dropSql(dropTableSql).createSql(sql + commentSql).cr(cr).build();
    }

    @Override
    protected String doGetDropTableSql(TableModel tableModel) {
        return String.format(ORACLE_DROP_TABLE_SQL, tableModel.getTableName(), tableModel.getTableName());
    }

    @Override
    protected String doGetInsertSql(TableModel tableModel, List<String> fields, List<String> values) {
        return null;
    }

    @Override
    public String getSegmentDataQuerySql(TableModel tableModel, int start, int length) {
        String template = " SELECT * FROM  \n" +
                "(  \n" +
                "SELECT A.*, ROWNUM RN  \n" +
                "FROM ( SELECT * FROM {tableName}  WHERE 1 = 1 ) A  \n" +
                "WHERE ROWNUM <= {end}  \n" +
                ")  \n" +
                "WHERE RN > {start}  ";
        return template.replace("{tableName}", tableModel.getTableName())
                .replace("{end}", String.valueOf(start + length)).replace("{start}", String.valueOf(start));
    }

    @Override
    public String getTableModelQuerySql(String tableName) {
        return null;
    }

    @Override
    public String getFieldModelQuerySql(String tableName) {
        return null;
    }


    private void checkTableName(TableModel tableModel) {
        String tableName = tableModel.getTableName();
        if (!StringUtils.hasText(tableName)) {
            throw new JobDefinitionException(" Table name is empty, please check, model:" + tableModel);
        }
        if (tableName.length() > 30) {
            throw new JobDefinitionException("The table name of Oracle cannot exceed 30 characters. Please check, tableName:" + tableName);
        }
        if (!RegexUtil.match(RegexUtil.ORACLE_NAME_REG, tableName)) {
            throw new JobDefinitionException("Does not meet Oracle's table name rules, tableName:" + tableName);
        }
    }
}
