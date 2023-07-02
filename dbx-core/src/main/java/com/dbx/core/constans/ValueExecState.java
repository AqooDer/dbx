package com.dbx.core.constans;

/**
 * @author Aqoo
 */
public enum ValueExecState {
    /**
     * 直接跳过执行，什么都不做
     */
    NONE,
    /**
     * 只是创建该数据，该数据不会被创建sql。
     */
    CREATE_VALUE,
    /**
     * 创建sql语句
     */
    CREATE_SQL,
    /**
     * 当目标数据库被连接时，执行sql语句的操作<br>
     * 当目标数据库被不能连接时，EXEC_SQL = Create_SQL
     */
    EXEC_SQL;
}
