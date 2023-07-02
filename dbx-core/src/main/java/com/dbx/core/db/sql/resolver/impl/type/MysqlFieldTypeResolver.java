package com.dbx.core.db.sql.resolver.impl.type;

import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.exception.JobExecuteException;
import com.dbx.core.util.jdbc.DbUtil;
import com.dbx.core.util.select.Selector;
import lombok.NonNull;

/**
 * @author Aqoo
 */
public class MysqlFieldTypeResolver implements FieldTypeResolver {

    private DataSourceMapping dataSourceMapping;


    public DataSourceMapping getDataSourceMapping() {
        return dataSourceMapping;
    }

    @Override
    public void setDataSourceMapping(DataSourceMapping dataSourceMapping) {
        this.dataSourceMapping = dataSourceMapping;
    }

    /**
     * 将java模型根据数据库类型转换成对应的数据库模型
     *
     * @param fieldJavaModel java数据模型
     * @return 数据库模型
     * @throws JobExecuteException 转换异常
     */
    @Override
    public FieldDbModel javaModel2DbModel(@NonNull FieldJavaModel fieldJavaModel) throws JobExecuteException {
        int length = fieldJavaModel.getLength() == null ? 0 : fieldJavaModel.getLength();
        int decimalDigits = fieldJavaModel.getDecimalDigits() == null ? 0 : fieldJavaModel.getDecimalDigits();
        FieldDbModel dbModel = DbUtil.java2Db(fieldJavaModel, () -> new Selector<FieldJavaType, String>(fieldJavaModel.getFieldJavaType())
                .match(test(matchJavaType(FieldJavaType.Short), dbType("smallint")))
                .match(test(matchJavaType(FieldJavaType.Integer), dbType("int")))
                .match(test(matchJavaType(FieldJavaType.Long), dbType("bigint")))
                .match(test(matchJavaType(FieldJavaType.Decimal), dbType("decimal(" + DbUtil.getDecimalPrecision(length, decimalDigits) + ")")))
                .match(test(matchJavaType(FieldJavaType.Double), dbType(format("double", length, decimalDigits))))
                .match(test(matchJavaType(FieldJavaType.FixedString), dbType("char(" + length + ")")))
                .match(test(matchJavaType(length < 2000, FieldJavaType.String), dbType("varchar(" + length + ")")))
                .match(test(matchJavaType(FieldJavaType.String), dbType("mediumtext")))
                .match(test(matchJavaType(FieldJavaType.Date), dbType("date")))
                .match(test(matchJavaType(FieldJavaType.Time), dbType("time")))
                .match(test(matchJavaType(FieldJavaType.DateTime), dbType("datetime")))
                .match(test(matchJavaType(FieldJavaType.Timestamp), dbType("datetime")))
                // timestamp只支持 1970-01-01 08:00:01 到2038-01-19 11:14:07 ，超出报错：Data truncation: Incorrect datetime value: '1956-12-01 00:00:00' for column 'jungrq' at row 1
                //.match(test(matchJavaType(FieldJavaType.Timestamp), dbType("timestamp")))
                .match(test(matchJavaType(FieldJavaType.FixedBytes), dbType("binary")))
                .match(test(matchJavaType(length < 65535, FieldJavaType.Bytes), dbType("varbinary")))
                .match(test(matchJavaType(FieldJavaType.Bytes), dbType("longblob")))
                .orElse(v -> " unknown "));
        unresolvedJavaType(fieldJavaModel ,dbModel);
        return dbModel;
    }

    /**
     * 将db模型根据数据库类型转换成对应的java模型
     * mysql : int=integer
     * 浮点数统一使用 Decimal(m,d) = numeric(m,d)  M<65
     * text < 65535  longtext < 2147483647  mediumtext< 16777215
     *
     * @param fieldDbModel db数据模型
     * @return java模型模型
     * @throws JobExecuteException 转换异常
     */
    @Override
    public FieldJavaModel dbModel2JavaModel(@NonNull FieldDbModel fieldDbModel) throws JobExecuteException {
        FieldJavaModel model = DbUtil.db2Java(fieldDbModel, () -> new Selector<String, FieldJavaType>(fieldDbModel.getType())
                .match(matchDbType("smallint"), javaType(FieldJavaType.Short))
                .match(matchDbType("int"), javaType(FieldJavaType.Integer))
                .match(matchDbType("bigint"), javaType(FieldJavaType.Long))
                .match(matchDbType("decimal", "numeric"), javaType(FieldJavaType.Decimal))
                .match(matchDbType("double", "float"), javaType(FieldJavaType.Double))
                .match(matchDbType("varchar", "mediumtext", "longtext", "text"), javaType(FieldJavaType.String))
                // 定长字符
                .match(matchDbType("char"), javaType(FieldJavaType.FixedString))
                .match(matchDbType("date"), javaType(FieldJavaType.Date))
                .match(matchDbType("time"), javaType(FieldJavaType.Time))
                .match(matchDbType("datetime"), javaType(FieldJavaType.DateTime))
                .match(matchDbType("timestamp"), javaType(FieldJavaType.Timestamp))
                // varbinary 0-65535  longblob 0-4G
                .match(matchDbType("longblob", "varbinary"), javaType(FieldJavaType.Bytes))
                .match(matchDbType("binary"), javaType(FieldJavaType.FixedBytes))
                .orElse(v -> FieldJavaType.NONE));
        unresolvedDbType(model , fieldDbModel );
        return model;
    }
}
