package com.dbx.test.unit;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.BasicJobRunner;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.db.data.ValueContext;
import com.dbx.core.db.data.ValueFormat;

/**
 * 拷贝数据测试
 *
 * @author Aqoo
 */
public class CopyTableTest implements MapperConfig {

    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args, MyMapperPropertyConfig.getMapperPropertyConfig(),
                CopyTableTest.class )).run();
    }

    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{UserMan.class};
    }

    @MapperTable(target = "user", source = "user", customFormatValue = UserFormat.class)
    public static class UserMan {

    }

    public static class UserFormat implements ValueFormat {

        @Override
        public void prepare(ValueContext valueContext) {
            //   这里可以预处理一些外部资源，比如查询数据库等，这里永不着。
        }

        @Override
        public Object format(TableFieldValueMapperDefinition fvm, ValueContext valueContext) {
            if ("sex".equals(fvm.getTargetField())) {
                String sex = valueContext.getSourceValue().get("sex").toString();
                return "男".equals(sex) ? 1 : 2;
            }
            if ("name".equals(fvm.getTargetField())) {
                return "中国的" + valueContext.getSourceValue().get("name").toString();
            }
            // 返回null，引擎将会从其他配置信息中获取数据
            return null;
        }

        @Override
        public void end(ValueContext valueContext) {

        }
    }
}
