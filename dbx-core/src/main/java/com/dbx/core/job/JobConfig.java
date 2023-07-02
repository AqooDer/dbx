package com.dbx.core.job;


import com.dbx.core.constans.DbType;
import com.dbx.core.db.DbTransferType;
import com.dbx.core.db.datasource.DataSourceConfig;
import com.fasterxml.jackson.databind.cfg.MapperConfig;

/**
 * 数据库迁移配置环境的基础指定配置
 *
 * @author Aqoo
 * @see MapperConfig
 */
public interface JobConfig {
    /**
     * 指定 数据转换的数据库类型
     *
     * @return 数据库对应类型
     */
    DbTransferType dbTransferType();

    /**
     * 是否开启ddl验证。
     * 该验证是将程序中生成的ddl模型与目标数据库中表模型进行对比，是否字段一致，如果不一致将提示报错。
     * 该功能使用在对程序的配置的检测功能。
     * 默认关闭 。当enableTargetSchemaVerify=true并且目标数据库可连接时生效
     *
     * @return
     */
    default boolean enableTargetSchemaVerify() {
        return false;
    }

    /**
     * 是否开启创建目标表功能
     * 默认开启，当enableCreateTable=true并且目标数据库可连接时生效
     *
     * @return true 禁用创建  false 创建表
     */
    default boolean enableCreateTable() {
        return true;
    }

    /**
     * 默认开启，当enableInsertData=true并且目标数据库可连接时生效
     *
     * @return
     */
    default boolean enableInsertData() {
        return true;
    }

    /**
     * 在插入某个表数据前 先清除该表的数据
     * 当insert数据后发现错误后需要第二次迁移数据时，比较有用。
     *
     * @return
     */
    default boolean enableClearTableDataBeforeInsert() {
        return true;
    }

    /**
     * 配置是否需要输sql脚本
     * <p>
     * 默认true
     *
     * @return
     */
    default boolean enableCreateSchemaScript() {
        return true;
    }

    default boolean enableCreateDataScript() {
        return true;
    }

    /**
     * 是否需要将 数据sql脚本合并到一个日志文件中显示。
     * 默认是按表名称创建日志文件
     *
     * @return
     */
    default boolean mergeDataScript() {
        return false;
    }

    /**
     * 配置源数据地址链接信息
     *
     * @return 返回源数据库链接信息
     */
    DataSourceConfig getSourceDataSourceConfig();

    /**
     * 配置 目标数据库链接信息
     * 如果不存在则不写入目标数据库
     *
     * @return 返回目标数据库链接信息
     */
    DataSourceConfig getTargetDataSourceConfig();

    /**
     * 默认库对应方法
     *
     * @param source 原始库类型
     * @param target 目标库类型
     * @return 数据库对应类型
     */
    default DbTransferType newDbTransferType(DbType source, DbType target) {
        return new DbTransferType() {
            @Override
            public DbType target() {
                return source;
            }

            @Override
            public DbType source() {
                return target;
            }
        };
    }
}
