package com.dbx.core.db.datasource;


import com.dbx.core.constans.DbType;

import javax.sql.DataSource;

/**
 * @author Aqoo
 */
public interface JobDataSource extends DataSource {
    /**
     * 当前数据库类型
     *
     * @return DbType
     */
    DbType getDbType();

    /**
     * 返回链接的url地址
     *
     * @return 当前链接的url地址
     */
    String getUrl();
}
