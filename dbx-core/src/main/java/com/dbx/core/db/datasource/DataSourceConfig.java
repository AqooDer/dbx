package com.dbx.core.db.datasource;

/**
 * @author Aqoo
 */
public interface DataSourceConfig {
    /**
     * 数据源名称
     *
     * @return 数据源名称
     */
    String getName();

    /**
     * 数据源url
     *
     * @return 数据源url
     */
    String getUrl();

    /**
     * 用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 数据源驱动
     *
     * @return 数据源驱动
     */
    String getDriver();
}
