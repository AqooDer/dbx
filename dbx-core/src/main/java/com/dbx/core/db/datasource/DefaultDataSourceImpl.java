package com.dbx.core.db.datasource;


import com.dbx.core.constans.DbType;
import com.dbx.core.exception.JobDataSourceException;
import com.dbx.core.util.jdbc.DbUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * 默认数据源链接，可根据实际情况替换
 *
 * @author Aqoo
 */
@Getter
@Setter
@ToString
public class DefaultDataSourceImpl implements JobDataSource {

    private String name;
    @NonNull
    private DbType type;
    @NonNull
    private String url;
    @NonNull
    private String driverName;
    private String username;
    private String password;
    private String schema;

    public DefaultDataSourceImpl(DataSourceConfig dataSourceConfig) {
        this.url = dataSourceConfig.getUrl();
        this.username = dataSourceConfig.getUsername();
        this.password = dataSourceConfig.getPassword();
        this.name = dataSourceConfig.getName();
        this.type = DbUtil.getDbType(url);
        this.driverName = dataSourceConfig.getDriver();
        if (!StringUtils.hasText(driverName)) {
            this.driverName = type.getDriver();
        }
        initDataSource();
    }

    private void initDataSource() throws JobDataSourceException {
        try {
            Class.forName(driverName);
            getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new JobDataSourceException(String.format("无法找到驱动类：%s", driverName));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new JobDataSourceException(String.format("连接异常：%s", this));
        }

    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLException("DataSource can't support getConnection method!");
    }


    @Override
    public PrintWriter getLogWriter() {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        DriverManager.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> face) throws SQLException {
        throw new SQLException("Can't support unwrap method!");
    }

    @Override
    public boolean isWrapperFor(Class<?> face) throws SQLException {
        throw new SQLException("Can't support isWrapperFor method!");
    }

    /**
     * Support from JDK7
     *
     * @since 1.7
     */
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("DataSource can't support getParentLogger method!");
    }

    @Override
    public DbType getDbType() {
        return type;
    }
}
