package com.dbx.test.source;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.MapperDataSourceConfig;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.BasicJobRunner;
import com.dbx.core.constans.DbType;
import com.dbx.core.db.DbTransferType;
import com.dbx.core.job.JobConfig;
import com.dbx.test.unit.MyMapperPropertyConfig;

/**
 * 获取原始库的索引 测试
 */
public class IndexOracleTest implements MapperConfig {
    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args, IndexOracleTest.getMapperPropertyConfig(),
                IndexOracleTest.class)).run();
    }


    public static JobConfig getMapperPropertyConfig() {
        MyMapperPropertyConfig config = new MyMapperPropertyConfig();

        config.setEnableCreateTable(false);
        config.setEnableInsertData(false);
        config.setEnableCreateSchemaScript(true);

        config.setDbTransferType(DbTransferType.instance(DbType.ORACLE, DbType.MYSQL));
        config.setSourceConfig(MapperDataSourceConfig.builder().name("gs_report")
                .url("jdbc:oracle:thin:@127.0.0.1:1521:oracle")
                .username("test").password("test").driver("com.mysql.cj.jdbc.Driver").build());
        return config;
    }


    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{EscApi.class};
    }

    @MapperTable(target = "user", source = "user")
    public static class EscApi {

    }
}
