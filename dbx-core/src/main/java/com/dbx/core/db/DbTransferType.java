package com.dbx.core.db;

import com.dbx.core.constans.DbType;

/**
 * 数据库对应类型
 *
 * @author Aqoo
 */
public interface DbTransferType {
    /**
     * 目标库的类型
     *
     * @return 数据库类型
     */
    DbType target();

    /**
     * 原库的类型
     *
     * @return 数据库类型
     */
    DbType source();


    /**
     * 判断是否是同一个类型的数据库
     *
     * @return true 是
     */
    default boolean isSameDb() {
        return target().equals(source());
    }


    static DbTransferType instance(DbType source, DbType target) {
        return new DbTransferType() {
            @Override
            public DbType target() {
                return target;
            }

            @Override
            public DbType source() {
                return source;
            }
        };
    }
}
