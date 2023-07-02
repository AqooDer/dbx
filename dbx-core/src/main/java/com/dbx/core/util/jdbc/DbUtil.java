package com.dbx.core.util.jdbc;

import cn.hutool.core.util.StrUtil;
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
        if (dbUrl.startsWith("jdbc:mysql:") || dbUrl.startsWith("jdbc:cobar:") || dbUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return DbType.mysql;
        } else if (dbUrl.startsWith("jdbc:mariadb:")) {
            return DbType.mariadb;
        } else if (dbUrl.startsWith("jdbc:oracle:") || dbUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return DbType.oracle;
        } else if (dbUrl.startsWith("jdbc:microsoft:") || dbUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return DbType.sqlserver;
        } else if (dbUrl.startsWith("jdbc:sqlserver:") || dbUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return DbType.sqlserver;
        } else if (dbUrl.startsWith("jdbc:postgresql:") || dbUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return DbType.postgresql;
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
        return l + StrUtil.COMMA + r;
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

    public static void main(String[] args) {
        System.out.println(decompose(new FieldDbModel(), "int"));
        System.out.println(decompose(new FieldDbModel(), "varchar(64)"));
        System.out.println(decompose(new FieldDbModel(), "float(23,12)"));
    }


   /* public static String getDbType(String rawUrl) {
        if (rawUrl == null) {
            return null;
        }

        if (rawUrl.startsWith("jdbc:derby:") || rawUrl.startsWith("jdbc:log4jdbc:derby:")) {
            return DERBY;
        } else if (rawUrl.startsWith("jdbc:mysql:") || rawUrl.startsWith("jdbc:cobar:")
                || rawUrl.startsWith("jdbc:log4jdbc:mysql:")) {
            return MYSQL;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB;
        } else if (rawUrl.startsWith("jdbc:oracle:") || rawUrl.startsWith("jdbc:log4jdbc:oracle:")) {
            return ORACLE;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE;
        } else if (rawUrl.startsWith("jdbc:microsoft:") || rawUrl.startsWith("jdbc:log4jdbc:microsoft:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sqlserver:") || rawUrl.startsWith("jdbc:log4jdbc:sqlserver:")) {
            return SQL_SERVER;
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:") || rawUrl.startsWith("jdbc:log4jdbc:sybase:")) {
            return SYBASE;
        } else if (rawUrl.startsWith("jdbc:jtds:") || rawUrl.startsWith("jdbc:log4jdbc:jtds:")) {
            return JTDS;
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return MOCK;
        } else if (rawUrl.startsWith("jdbc:postgresql:") || rawUrl.startsWith("jdbc:log4jdbc:postgresql:")) {
            return POSTGRESQL;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return ENTERPRISEDB;
        } else if (rawUrl.startsWith("jdbc:hsqldb:") || rawUrl.startsWith("jdbc:log4jdbc:hsqldb:")) {
            return HSQL;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return ODPS;
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            return DB2;
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return "ingres";
        } else if (rawUrl.startsWith("jdbc:h2:") || rawUrl.startsWith("jdbc:log4jdbc:h2:")) {
            return H2;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return "mckoi";
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return "cloudscape";
        } else if (rawUrl.startsWith("jdbc:informix-sqli:") || rawUrl.startsWith("jdbc:log4jdbc:informix-sqli:")) {
            return "informix";
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return "timesten";
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return "as400";
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return "sapdb";
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return "JSQLConnect";
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return "JTurbo";
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return "firebirdsql";
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return "interbase";
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return "pointbase";
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return "edbc";
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return "mimer";
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE;
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return HIVE;
        } else if (rawUrl.startsWith("jdbc:phoenix:")) {
            return PHOENIX;
        } else {
            return null;
        }
    }*/
}
