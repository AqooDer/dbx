package com.dbx.test.unit;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.DdlConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.BasicJobRunner;
import com.dbx.core.constans.FieldJavaType;

/**
 * 拷贝数据测试
 *
 * @author Aqoo
 */
public class SplitTableTest implements MapperConfig {

    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args, MyMapperPropertyConfig.getMapperPropertyConfig(), SplitTableTest.class)).run();
    }

    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{User.class};
    }

    @MapperTable(target = "user", source = "user", children = {Age.class}, excludeFields = {"age"})
    public static class User {

    }

    @MapperTable(target = "age")
    @MapperField(target = "id", superSource = "id")
    @MapperField(target = "name", superSource = "name")
    @MapperField(target = "age", superSource = "age")
    @MapperField(target = "address", ddl = @DdlConfig(type = FieldJavaType.String, length = 4, content = "地址"))
    public static class Age {

    }
}
