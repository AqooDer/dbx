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
        sqlGeneratorMap.put(DbType.mysql, MysqlSqlGenerator::new);
        sqlGeneratorMap.put(DbType.postgresql, PostgreSqlGenerator::new);
        sqlGeneratorMap.put(DbType.oracle, OracleSqlGenerator::new);
    }

    static {
        FieldValueMap.put(DbType.mysql, DbType.mysql, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.mysql, DbType.postgresql, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.mysql, DbType.oracle, () -> new OracleTargetFieldValueResolve(new DefaultFieldValueResolver()));
        //FieldValueMap.put(mysql, sqlserver, DefaultFieldValueResolver::new);

        FieldValueMap.put(DbType.postgresql, DbType.postgresql, DefaultFieldValueResolver::new);
        FieldValueMap.put(DbType.postgresql, DbType.mysql, DefaultFieldValueResolver::new);
        //FieldValueMap.put(postgresql, oracle, DefaultFieldValueResolver::new);
        //FieldValueMap.put(postgresql, sqlserver, DefaultFieldValueResolver::new);

        //FieldValueMap.put(oracle, oracle, () -> new OracleFieldValueResolver(new DefaultFieldValueResolver()));
        FieldValueMap.put(DbType.oracle, DbType.mysql, () -> new OracleSourceFieldValueResolver(new DefaultFieldValueResolver()));
        FieldValueMap.put(DbType.oracle, DbType.postgresql, () -> new OracleSourceFieldValueResolver(new DefaultFieldValueResolver()));
        //FieldValueMap.put(oracle, sqlserver, () -> new OracleFieldValueResolver(new DefaultFieldValueResolver()));
    }

    static {
        fieldTypeResolverMap.put(DbType.mysql, MysqlFieldTypeResolver::new);
        fieldTypeResolverMap.put(DbType.postgresql, PostgreSqlFieldTypeResolver::new);
        fieldTypeResolverMap.put(DbType.oracle, OracleFieldTypeResolver::new);

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
