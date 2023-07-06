package com.dbx.core.util.jdbc;

import cn.hutool.core.text.StrPool;
import com.dbx.core.constans.DbType;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.constans.JdbcConstants;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.exception.JobException;

import java.util.function.Supplier;

/**
 * @author Aqoo
 */
public class DbUtil extends JdbcConstants {

    public static DbType getDbType(String dbUrl) {
        if (dbUrl == null) {
            throw new JobException("the jdbc connection url is null , please checked. ");
        }
        if (dbUrl.startsWith("jdbc:MYSQL:") || dbUrl.startsWith("jdbc:cobar:") || dbUrl.startsWith("jdbc:log4jdbc:MYSQL:")) {
            return DbType.MYSQL;
        } else if (dbUrl.startsWith("jdbc:MARIADB:")) {
            return DbType.MARIADB;
        } else if (dbUrl.startsWith("jdbc:ORACLE:") || dbUrl.startsWith("jdbc:log4jdbc:ORACLE:")) {
            return DbType.ORACLE;
        } else if (dbUrl.startsWith("jdbc:microsoft:") || dbUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return DbType.SQLSERVER;
        } else if (dbUrl.startsWith("jdbc:SQLSERVER:") || dbUrl.startsWith("jdbc:log4jdbc:SQLSERVER:")) {
            return DbType.SQLSERVER;
        } else if (dbUrl.startsWith("jdbc:POSTGRESQL:") || dbUrl.startsWith("jdbc:log4jdbc:POSTGRESQL:")) {
            return DbType.POSTGRESQL;
        } else {
            throw new JobException("the jdbc connection url is not be support. ");
        }
    }


    public static String getDdlType(FieldDbModel model) {
        String type = model.getType();
        Integer length = model.getLength();
        Integer decimalDigits = model.getDecimalDigits();
        if (length != null && length != 0) {
            if (decimalDigits != null && decimalDigits != 0) {
                return type + "(" + length + "," + decimalDigits + ")";
            }
            return type + "(" + length + ")";
        }
        return type;
    }


    public static FieldDbModel java2Db(FieldJavaModel fieldJavaModel, Supplier<String> supplier) {
        FieldDbModel fieldDbModel = new FieldDbModel();
        fieldDbModel.setFieldName(fieldJavaModel.getFieldName());
        fieldDbModel.setContent(fieldJavaModel.getContent());
        fieldDbModel.setNullable(fieldJavaModel.getNullable());
        fieldDbModel.setPk(fieldJavaModel.getPk());
        fieldDbModel.setDefaultValue(fieldJavaModel.getDefaultValue());
        return DbUtil.decompose(fieldDbModel, supplier.get());
    }



    public static FieldJavaModel db2Java(FieldDbModel fieldDbModel, Supplier<FieldJavaType> getJavaType) {
        FieldJavaModel fieldJavaModel = new FieldJavaModel();
        fieldJavaModel.setFieldName(fieldDbModel.getFieldName());
        fieldJavaModel.setContent(fieldDbModel.getContent());
        fieldJavaModel.setNullable(fieldDbModel.getNullable());
        fieldJavaModel.setPk(fieldDbModel.getPk());
        fieldJavaModel.setLength(fieldDbModel.getLength());
        fieldJavaModel.setDecimalDigits(fieldDbModel.getDecimalDigits());
        fieldJavaModel.setDefaultValue(fieldDbModel.getDefaultValue());

        fieldJavaModel.setFieldJavaType(getJavaType.get());
        return fieldJavaModel;
    }


    public static String getDecimalPrecision(int length, int precision) {
        int l = length >= 19 ? 19 : Math.max(length, 14);
        int r = precision >= l || precision <= 0 ? 2 : precision;
        return l + StrPool.COMMA + r;
    }


    public static FieldDbModel decompose(FieldDbModel dbModel, String type) {
        int index = type.indexOf("(");
        if (index > -1) {
            String len = type.substring(index + 1, type.length() - 1);
            index = len.indexOf(",");
            if (index > -1) {
                dbModel.setLength(Integer.valueOf(len.substring(0, index)));
                dbModel.setDecimalDigits(Integer.valueOf(len.substring(index + 1)));
            } else {
                dbModel.setLength(Integer.valueOf(len));
            }
        }
        dbModel.setType(type);
        return dbModel;
    }

    public static FieldJavaModel getDefaultIdFieldJavaModel(String fieldName) {
        FieldJavaModel fieldJavaModel = new FieldJavaModel();
        fieldJavaModel.setFieldName(fieldName);
        fieldJavaModel.setFieldJavaType(FieldJavaType.String);
        fieldJavaModel.setContent("主键id");
        fieldJavaModel.setNullable(false);
        fieldJavaModel.setPk(true);
        fieldJavaModel.setLength(64);
        return fieldJavaModel;
    }

}
