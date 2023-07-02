package com.dbx.core.db.sql.resolver.impl.type;

import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.constans.OracleFieldTypeEnum;
import com.dbx.core.db.DataSourceMapping;
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
public class OracleFieldTypeResolver implements FieldTypeResolver {

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
                //number(m,n) m=1 to 38
                //n=-84 to 127 可变长的数值列，允许0、正值及负值，m是所有有效数字的位数，n是小数点以后的位数。
                .match(test(matchJavaType(FieldJavaType.Short), dbType(String.format("NUMBER(%s,0)", length))))
                .match(test(matchJavaType(FieldJavaType.Integer), dbType(String.format("NUMBER(%s,0)", length))))
                .match(test(matchJavaType(FieldJavaType.Long), dbType(String.format("NUMBER(%s,0)", length))))
                .match(test(matchJavaType(length > 38, FieldJavaType.Long), dbType(String.format("REAL(%s,0)", length))))
                .match(test(matchJavaType(FieldJavaType.Decimal), dbType("NUMBER(" + DbUtil.getDecimalPrecision(length, decimalDigits) + ")")))
                //	"float" FLOAT(122),
                .match(test(matchJavaType(FieldJavaType.Double), dbType((String.format("FLOAT(%s)", length)))))
                // 官方文档给的nvarchar2的最大长度是4000bytes,如果是英文的话应该是4000个,中文的话应该是2000个
                .match(test(matchJavaType(length < 2000, FieldJavaType.String), dbType("NVARCHAR2(" + length + ")")))
                .match(test(matchJavaType(length < 2000, FieldJavaType.FixedString), dbType("NCHAR(" + length + ")")))
                .match(test(matchJavaType(FieldJavaType.String), dbType("NCLOB")))
                .match(test(matchJavaType(FieldJavaType.FixedString), dbType("NCLOB")))
                .match(test(matchJavaType(FieldJavaType.Date), dbType("DATE")))
                .match(test(matchJavaType(FieldJavaType.Time), dbType("DATE")))
                .match(test(matchJavaType(FieldJavaType.DateTime), dbType("DATE")))
                .match(test(matchJavaType(FieldJavaType.Timestamp), dbType("TIMESTAMP")))
                .match(test(matchJavaType(FieldJavaType.Bytes), dbType("BLOB")))
                .match(test(matchJavaType(FieldJavaType.FixedBytes), dbType("BLOB")))
                .orElse(v -> " unknown "));
        unresolvedJavaType(fieldJavaModel ,dbModel);
        return dbModel;
    }

    /**
     * @param fieldDbModel db数据模型
     * @return java模型模型
     * @throws JobExecuteException 转换异常
     */
    @Override
    public FieldJavaModel dbModel2JavaModel(@NonNull FieldDbModel fieldDbModel) throws JobExecuteException {
        int length = fieldDbModel.getLength() == null ? 0 : fieldDbModel.getLength();
        int decimalDigits = fieldDbModel.getDecimalDigits() == null ? 0 : fieldDbModel.getDecimalDigits();
        FieldJavaModel model = DbUtil.db2Java(fieldDbModel, () -> new Selector<String, FieldJavaType>(fieldDbModel.getType())
                .match(matchDbType(length > 0 && decimalDigits > 0, "number"), javaType(FieldJavaType.Decimal))
                .match(matchDbType(length <= 4, "number"), javaType(FieldJavaType.Short))
                .match(matchDbType("smallint"), javaType(FieldJavaType.Short))
                .match(matchDbType(length <= 8, "number"), javaType(FieldJavaType.Integer))
                .match(matchDbType("int"), javaType(FieldJavaType.Integer))
                .match(matchDbType("float", "real"), javaType(FieldJavaType.Double))
                .match(matchDbType("number"), javaType(FieldJavaType.Long))
                .match(matchDbType("char","nchar"), javaType(FieldJavaType.FixedString))
                .match(matchDbType( "varchar2", "nvarchar2", "clob", "nclob"), javaType(FieldJavaType.String))
                .match(matchDbType(OracleFieldTypeEnum.raw.name()), v -> {
                    fieldDbModel.setLength(fieldDbModel.getLength() * 2); // raw的长度是16，则16进制的长度为32。
                    return FieldJavaType.String;
                })
                .match(matchDbType("date"), javaType(FieldJavaType.Date))
                .match(matchDbType("timestamp"), javaType(FieldJavaType.Timestamp))
                .match(matchDbType("blob"), javaType(FieldJavaType.Bytes))
                .orElse(v -> FieldJavaType.NONE));
        unresolvedDbType(model , fieldDbModel );
        return model;
    }
}
