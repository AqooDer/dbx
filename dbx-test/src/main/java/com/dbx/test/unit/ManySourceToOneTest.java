package com.dbx.test.unit;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.bean.config.annotation.UseDdl;
import com.dbx.core.BasicJobRunner;

/***
 * 测试将多个源表的数据迁移到单个表上面
 * 测试模型：
 *
 * @author Aqoo
 */
public class ManySourceToOneTest implements MapperConfig {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args, MyMapperPropertyConfig.getMapperPropertyConfig(), ManySourceToOneTest.class)).run();
    }

    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{UserWoman.class, UserMan.class};
    }

    /**
     * 将字段 sex_m -> 转换成 sex。 在这种情况下需要使用 excludeFields排除原始字段。
     */
    @MapperTable(target = "user", source = "user_man", excludeFields = {"sex_m"})
    @MapperField(target = "sex", source = "sex_m")
    @UseDdl
    public static class UserMan {

    }


    @MapperTable(target = "user", source = "user_woman")
    @MapperField(target = "sex", source = "sex_w")
    public static class UserWoman {

    }


}
