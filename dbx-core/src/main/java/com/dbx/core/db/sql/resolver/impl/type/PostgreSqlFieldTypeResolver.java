package com.dbx.core.db.sql.resolver.impl.type;

import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.exception.JobExecuteException;
import com.dbx.core.util.jdbc.DbUtil;
import com.dbx.core.util.select.Matcher;
import com.dbx.core.util.select.Selector;

/**
 * @author Aqoo
 */
public class PostgreSqlFieldTypeResolver implements FieldTypeResolver {

    private DataSourceMapping dataSourceMapping;

    @Override
    public void setDataSourceMapping(DataSourceMapping dataSourceMapping) {
        this.dataSourceMapping = dataSourceMapping;
    }

    @Override
    public FieldDbModel javaModel2DbModel(FieldJavaModel fieldJavaModel) throws JobExecuteException {
        int length = fieldJavaModel.getLength() == null ? 0 : fieldJavaModel.getLength();
        int decimalDigits = fieldJavaModel.getDecimalDigits() == null ? 0 : fieldJavaModel.getDecimalDigits();
        FieldDbModel dbModel = DbUtil.java2Db(fieldJavaModel, () -> new Selector<FieldJavaType, String>(fieldJavaModel.getFieldJavaType())
                .match(test(matchJavaType(length < 10485760, FieldJavaType.String), dbType("varchar(" + length + ")")))
                .match(test(matchJavaType(FieldJavaType.String), dbType("text")))
                .match(test(matchJavaType(FieldJavaType.DateTime), dbType("timestamp")))
                .match(test(matchJavaType(FieldJavaType.Timestamp), dbType("timestamp")))
                .match(test(matchJavaType(FieldJavaType.Integer), dbType("int4")))
                .match(test(matchJavaType(FieldJavaType.Short), dbType("int2")))
                .match(test(matchJavaType(FieldJavaType.Long), dbType("int8")))
                .match(test(matchJavaType(FieldJavaType.Decimal), dbType("decimal(" + DbUtil.getDecimalPrecision(length, decimalDigits) + ")")))
                .match(test(matchJavaType(FieldJavaType.Double), dbType("decimal(" + DbUtil.getDecimalPrecision(length, decimalDigits) + ")")))
                .match(test(matchJavaType(FieldJavaType.Date), dbType("date")))
                .match(test(matchJavaType(FieldJavaType.Time), dbType("time")))
                .orElse(v -> " unknown "));
        unresolvedJavaType(fieldJavaModel ,dbModel);
        return dbModel;
    }

    @Override
    public FieldJavaModel dbModel2JavaModel(FieldDbModel fieldDbModel) throws JobExecuteException {
        FieldJavaModel model = DbUtil.db2Java(fieldDbModel, () -> new Selector<String, FieldJavaType>(fieldDbModel.getType())
                .match(Matcher.of(matchDbType("char", "text", "json", "enum", "varchar"), javaType(FieldJavaType.String)))
                // bit -> BOOLEAN
                .match(Matcher.of(matchDbType("int2", "bit", "boolean"), javaType(FieldJavaType.Short)))
                .match(Matcher.of(matchDbType("int4"), javaType(FieldJavaType.Integer)))
                .match(Matcher.of(matchDbType("int8", "bigint"), javaType(FieldJavaType.Long)))
                .match(Matcher.of(matchDbType("decimal", "numeric"), javaType(FieldJavaType.Decimal)))
                .match(Matcher.of(matchDbType("double", "float"), javaType(FieldJavaType.Double)))
                .match(Matcher.of(matchDbType("date"), javaType(FieldJavaType.Date)))
                .match(Matcher.of(matchDbType("time"), javaType(FieldJavaType.Time)))
                .match(Matcher.of(matchDbType("timestamp"), javaType(FieldJavaType.Timestamp)))
                .orElse(v -> FieldJavaType.NONE));
        unresolvedDbType(model , fieldDbModel );
        return model;
    }
}
