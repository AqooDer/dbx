package com.dbx.core.constans;

/**
 * 数据库类型
 *
 * @author Aqoo
 */
public enum DbType {
    /**
     * mysql
     */
    mysql(JdbcConstants.MYSQL, JdbcConstants.MYSQL_DRIVER),

    mariadb(JdbcConstants.MARIADB, JdbcConstants.MARIADB_DRIVER),

    oracle(JdbcConstants.ORACLE, JdbcConstants.ORACLE_DRIVER),

    sqlserver(JdbcConstants.SQL_SERVER, JdbcConstants.SQL_SERVER_DRIVER),

    postgresql(JdbcConstants.POSTGRESQL, JdbcConstants.POSTGRESQL_DRIVER),
    ;

    /**
     * 默认驱动类
     */
    private final String name;
    private final String driver;

    DbType(String name, String driver) {
        this.driver = driver;
        this.name = name;
    }

    public String getDriver() {
        return driver;
    }


    public static DbType getBy(String type) {
        for (DbType value : DbType.values()) {
            if (value.name().equalsIgnoreCase(type)) {
                return value;
            }
        }
        return null;
    }
}
