package com.dbx.test.unit;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.DdlConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.bean.config.annotation.UseDdl;
import com.dbx.bean.config.support.ValueDefaultType;
import com.dbx.core.BasicJobRunner;
import com.dbx.core.constans.FieldJavaType;

/**
 * @author Aqoo
 */
public class NoneSourceDbTest implements MapperConfig {

    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args,MyMapperPropertyConfig.getMapperPropertyConfig(),   NoneSourceDbTest.class)).run();
    }

    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{CarType1.class, CarType2.class, CarType3.class};
    }

    @MapperTable(target = "car_type")
    @MapperField(target = "id", id = true, defaultFormatValue = ValueDefaultType.UUID_NO_LINE)
    @MapperField(target = "type_", defaultValue = "奥迪", ddl = @DdlConfig(type = FieldJavaType.String, content = "汽车类型", length = 10))
    @UseDdl
    public static class CarType1 {

    }

    @MapperTable(target = "car_type")
    @MapperField(target = "id", id = true, defaultFormatValue = ValueDefaultType.UUID_NO_LINE)
    @MapperField(target = "type_", defaultValue = "宝马")
    public static class CarType2 {

    }

    @MapperTable(target = "car_type")
    @MapperField(target = "id", id = true, defaultFormatValue = ValueDefaultType.UUID_NO_LINE)
    @MapperField(target = "type_", defaultValue = "奔驰")
    public static class CarType3 {

    }
}
