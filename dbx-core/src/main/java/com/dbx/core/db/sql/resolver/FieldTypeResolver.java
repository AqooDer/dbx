package com.dbx.core.db.sql.resolver;


import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.exception.JobExecuteException;
import com.dbx.core.util.select.Matcher;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 用于解析 field 的ddl映射关系
 * <p>
 * 注意:
 * <p>
 * 1.由于数据库类型太多，无法全部适配并
 * <p>
 * 2.在适配的过程中，可能会丢失部分数据库类型，进行向上升级
 *
 * @author Aqoo
 */
public interface FieldTypeResolver {

    void setDataSourceMapping(DataSourceMapping dataSourceMapping);

    /**
     * 将java模型转换成db模型
     *
     * @param fieldJavaModel java 数据模型
     * @return FieldDbModel
     * @throws JobExecuteException sql异常
     */
    FieldDbModel javaModel2DbModel(FieldJavaModel fieldJavaModel) throws JobExecuteException;

    /**
     * 将db模型转化成java模型
     *
     * @param fieldDbModel db模型
     * @return FieldJavaModel
     * @throws JobExecuteException sql异常
     */
    FieldJavaModel dbModel2JavaModel(FieldDbModel fieldDbModel) throws JobExecuteException;

    default Matcher<FieldJavaType, String> test(Predicate<FieldJavaType> predicate, Function<FieldJavaType, String> function) {
        return Matcher.of(predicate, function);
    }

    /**
     * 提供的默认数据转换类
     *
     * @param bool
     * @param values
     * @return
     */
    default Predicate<FieldJavaType> matchJavaType(Boolean bool, FieldJavaType... values) {
        return (s -> {
            if (bool) {
                for (FieldJavaType value : values) {
                    if (s == value) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    default Predicate<FieldJavaType> matchJavaType(FieldJavaType... values) {
        return (s -> {
            for (FieldJavaType value : values) {
                if (s == value) {
                    return true;
                }
            }
            return false;
        });
    }

    default Function<FieldJavaType, String> dbType(String value) {
        return v -> value;
    }

    default Predicate<String> matchDbType(Boolean bool, String value) {
        return (s -> {
            if (bool) {
                if (s.equals(value)) {
                    return true;
                }
            }
            return false;
        });
    }

    default Predicate<String> matchDbType(String... values) {
        return (s -> {
            for (String value : values) {
                if (s.contains("(")) {
                    if (s.contains(value)) {
                        return true;
                    }
                }
                if (s.equals(value)) {
                    return true;
                }
            }
            return false;
        });
    }

    default Function<String, FieldJavaType> javaType(FieldJavaType value) {
        return v -> value;
    }

    default String format(String type, int length, int scale) {
        if (length == 0) {
            return type;
        }
        if (scale == 0) {
            return type + "(" + length + ")";
        }
        return type + "(" + length + "," + scale + ")";
    }

    default String formatOracle(String type, int length, int scale) {
        if (length == 0) {
            return type;
        }
        return type + "(" + length + "," + scale + ")";
    }

    default void unresolvedJavaType(FieldJavaModel javaModel, FieldDbModel dbModel) {
        if (" unknown ".equals(dbModel.getType())) {
            throw new JobExecuteException(String.format(" the field '%s' unresolved unknown type , the java field type:%s ", javaModel.getFieldName(), javaModel.getFieldJavaType()));
        }
    }

    default void unresolvedDbType(FieldJavaModel javaModel, FieldDbModel dbModel) {
        if (FieldJavaType.NONE == javaModel.getFieldJavaType()) {
            throw new JobExecuteException(String.format(" the field '%s' unresolved unknown type '%s' ", dbModel.getFieldName(), dbModel.getType()));
        }
    }
}
