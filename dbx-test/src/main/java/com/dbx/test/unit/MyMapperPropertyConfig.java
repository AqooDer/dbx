package com.dbx.test.unit;

import com.dbx.bean.config.MapperDataSourceConfig;
import com.dbx.core.constans.DbType;
import com.dbx.core.job.JobConfig;
import com.dbx.core.db.DbTransferType;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Aqoo
 */
@Getter
@Setter
public class MyMapperPropertyConfig implements JobConfig {
    private DbTransferType dbTransferType;

    private MapperDataSourceConfig sourceConfig;

    private MapperDataSourceConfig targetConfig;

    /**
     * 是否开启sql检查
     * true 开启
     */
    private boolean enableTargetSchemaVerify = false;

    private boolean enableCreateTable = false;
    private boolean enableInsertData = true;


    private boolean enableCreateSchemaScript = true;

    private boolean enableCreateDataScript = true;

    private boolean mergeDataScript = false;


    @Override
    public DbTransferType dbTransferType() {
        return dbTransferType;
    }

    @Override
    public boolean enableTargetSchemaVerify() {
        return enableTargetSchemaVerify;
    }

    @Override
    public boolean enableCreateTable() {
        return enableCreateTable;
    }

    @Override
    public boolean enableInsertData() {
        return enableInsertData;
    }

    @Override
    public boolean enableCreateSchemaScript() {
        return enableCreateSchemaScript;
    }

    @Override
    public boolean enableCreateDataScript() {
        return enableCreateDataScript;
    }

    @Override
    public boolean mergeDataScript() {
        return mergeDataScript;
    }

    @Override
    public MapperDataSourceConfig getSourceDataSourceConfig() {
        return sourceConfig;
    }

    @Override
    public MapperDataSourceConfig getTargetDataSourceConfig() {
        return targetConfig;
    }


    public static JobConfig getMapperPropertyConfig() {
        MyMapperPropertyConfig config = new MyMapperPropertyConfig();
        config.setDbTransferType(DbTransferType.instance(DbType.MYSQL, DbType.MYSQL));

        config.setSourceConfig(MapperDataSourceConfig.builder().name("gs_report")
                .url("jdbc:MYSQL://127.0.0.1:3306/unit_s?useOldAliasMetadataBehavior=true&zeroDateTimeBehavior=round&useSSL=false&allowPublicKeyRetrieval=true")
                .username("root").password("1qaz2wsx").driver("com.MYSQL.cj.jdbc.Driver").build());

        config.setTargetConfig(MapperDataSourceConfig.builder().name("jc_report_test")
                .url("jdbc:MYSQL://127.0.0.1:3306/unit_t?useOldAliasMetadataBehavior=true&useSSL=false&allowPublicKeyRetrieval=true")
                .driver("com.MYSQL.cj.jdbc.Driver").username("root").password("1qaz2wsx").build());
        return config;
    }
}
