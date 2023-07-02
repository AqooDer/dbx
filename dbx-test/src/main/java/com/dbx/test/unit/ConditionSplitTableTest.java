package com.dbx.test.unit;

import com.dbx.bean.AnnotationJobFactory;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.core.BasicJobRunner;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.constans.ValueExecState;
import com.dbx.core.db.data.ValueContext;
import com.dbx.core.db.data.ValueFormat;

/**
 * @author Aqoo
 */
public class ConditionSplitTableTest implements MapperConfig {
    public static void main(String[] args) throws Exception {
        new BasicJobRunner(new AnnotationJobFactory("ms", args,
                MyMapperPropertyConfig.getMapperPropertyConfig(),  ConditionSplitTableTest.class)).run();
    }

    @Override
    public Class<?>[] getMapperConfigs() {
        return new Class[]{ConditionSplitTableTest.User.class};
    }

    @MapperTable(target = "user", source = "user", children = {ConditionSplitTableTest.AgeNan.class, ConditionSplitTableTest.AgeNv.class}, excludeFields = {"age"})
    public static class User {

    }

    @MapperTable(target = "age_nan", customFormatValue = UserFormat.class)
    @MapperField(target = "id", superSource = "id")
    @MapperField(target = "name", superSource = "name")
    @MapperField(target = "age", superSource = "age")
    public static class AgeNan {

    }

    @MapperTable(target = "age_nv", customFormatValue = UserFormat.class)
    @MapperField(target = "id", superSource = "id")
    @MapperField(target = "name", superSource = "name")
    @MapperField(target = "age", superSource = "age")
    public static class AgeNv {

    }


    public static class UserFormat implements ValueFormat {

        @Override
        public void prepare(ValueContext valueContext) {
            if (valueContext.getTableRowValueMapperDefinition().getId().equals(AgeNan.class.getName()) && !valueContext.getParent().getSourceValue().get("sex").equals("男")) {
                valueContext.getValueState().setValueExecState(ValueExecState.NONE);
            }
            if (valueContext.getTableRowValueMapperDefinition().getId().equals(AgeNv.class.getName()) && !valueContext.getParent().getSourceValue().get("sex").equals("女")) {
                valueContext.getValueState().setValueExecState(ValueExecState.NONE);
            }
        }

        @Override
        public Object format(TableFieldValueMapperDefinition fvm, ValueContext valueContext) {
            return null;
        }

        @Override
        public void end(ValueContext valueContext) {

        }
    }
}
