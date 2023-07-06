package com.dbx.core.db.sql;


import com.dbx.core.constans.DbType;
import com.dbx.core.db.sql.generator.SqlGenerator;
import com.dbx.core.db.sql.generator.impl.MysqlSqlGenerator;
import com.dbx.core.db.sql.generator.impl.OracleSqlGenerator;
import com.dbx.core.db.sql.generator.impl.PostgreSqlGenerator;
import com.dbx.core.db.sql.resolver.FieldTypeResolver;
import com.dbx.core.db.sql.resolver.FieldValueResolver;
import com.dbx.core.db.sql.resolver.impl.type.MysqlFieldTypeResolver;
import com.dbx.core.db.sql.resolver.impl.type.OracleFieldTypeResolver;
import com.dbx.core.db.sql.resolver.impl.type.PostgreSqlFieldTypeResolver;
import com.dbx.core.db.sql.resolver.impl.value.DefaultFieldValueResolver;
import com.dbx.core.db.sql.resolver.impl.value.OracleSourceFieldValueResolver;
import com.dbx.core.db.sql.resolver.impl.value.OracleTargetFieldValueResolve;
import com.dbx.core.exception.JobException;
import com.dbx.core.util.DoubleKeyMap;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author Aqoo
 */
public final class SqlFactory {

    public static Map<DbType, Supplier<SqlGenerator>> sqlGeneratorMap = new HashMap<>();

    public static DoubleKeyMap<DbType, DbType, Supplier<FieldValueResolver>> FieldValueMap = new DoubleKeyMap<>();

    public static Map<DbType, Supplier<FieldTypeResolver>> fieldTypeResolverMap = new HashMap<>();

    static {
        sqlGeneratorMap.put(DbType.MYSQL, MysqlSqlGenerator::new);
        sqlGeneratorMap.put(DbType.POSTGRESQL, PostgreSqlGenerator::new);
        sqlGeneratorMap.put(DbType.ORACLE, OracleSqlGenerator::new);
    }

    static {
        FieldValueMap.put(DbType.MYSQL, DbType.MYSQL, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.MYSQL, DbType.POSTGRESQL, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.MYSQL, DbType.ORACLE, () -> new OracleTargetFieldValueResolve(new DefaultFieldValueResolver()));
        //FieldValueMap.put(MYSQL, SQLSERVER, DefaultFieldValueResolver::new);

        FieldValueMap.put(DbType.POSTGRESQL, DbType.POSTGRESQL, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.POSTGRESQL, DbType.MYSQL, DefaultFieldValueResolver::new);
        //FieldValueMap.put(POSTGRESQL, ORACLE, DefaultFieldValueResolver::new);
        //FieldValueMap.put(POSTGRESQL, SQLSERVER, DefaultFieldValueResolver::new);

        //FieldValueMap.put(ORACLE, ORACLE, () -> new OracleFieldValueResolver(new DefaultFieldValueResolver()));
        FieldValueMap.put(DbType.ORACLE, DbType.MYSQL, () -> new OracleSourceFieldValueResolver(new DefaultFieldValueResolver()));
        FieldValueMap.put(DbType.ORACLE, DbType.POSTGRESQL, () -> new OracleSourceFieldValueResolver(new DefaultFieldValueResolver()));
        //FieldValueMap.put(ORACLE, SQLSERVER, () -> new OracleFieldValueResolver(new DefaultFieldValueResolver()));
    }

    static {
        fieldTypeResolverMap.put(DbType.MYSQL, MysqlFieldTypeResolver::new);
        fieldTypeResolverMap.put(DbType.POSTGRESQL, PostgreSqlFieldTypeResolver::new);
        fieldTypeResolverMap.put(DbType.ORACLE, OracleFieldTypeResolver::new);

    }

    public static FieldTypeResolver getFieldTypeResolverInstance(DbType type) throws JobException {
        if (!fieldTypeResolverMap.containsKey(type)) {
            throw new JobException("实例不存在");
        }
        return fieldTypeResolverMap.get(type).get();
    }


    public static SqlGenerator getSqlGeneratorInstance(DbType type) throws JobException {
        if (!sqlGeneratorMap.containsKey(type)) {
            throw new JobException("实例不存在");
        }
        return sqlGeneratorMap.get(type).get();
    }

    public static FieldValueResolver getFieldValueResolver(DbType source, DbType target) throws JobException {
        if (!FieldValueMap.containsKey(source, target)) {
            throw new JobException("实例不存在");
        }
        return FieldValueMap.get(source, target).get();
    }
}
