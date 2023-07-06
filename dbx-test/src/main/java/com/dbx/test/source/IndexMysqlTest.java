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
public class IndexMysqlTest implements MapperConfig {
    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args, IndexMysqlTest.getMapperPropertyConfig(),
                IndexMysqlTest.class)).run();
    }


    public static JobConfig getMapperPropertyConfig() {
        MyMapperPropertyConfig config = new MyMapperPropertyConfig();

        config.setEnableCreateTable(false);
        config.setEnableInsertData(false);
        config.setEnableCreateSchemaScript(true);

        config.setDbTransferType(DbTransferType.instance(DbType.MYSQL, DbType.MYSQL));
        config.setSourceConfig(MapperDataSourceConfig.builder().name("gs_report")
                .url("jdbc:MYSQL://127.0.0.1:3306/unit_s?useOldAliasMetadataBehavior=true&zeroDateTimeBehavior=round&useSSL=false&allowPublicKeyRetrieval=true")
                .username("root").password("1qaz2wsx").driver("com.MYSQL.cj.jdbc.Driver").build());
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
