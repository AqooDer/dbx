package com.dbx.core.db.sql.resolver.impl.value;

import cn.hutool.core.date.DateUtil;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.db.DbTransferType;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.sql.resolver.FieldValueResolver;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class OracleTargetFieldValueResolve implements FieldValueResolver {

    private final DefaultFieldValueResolver defaultFieldValueResolver;

    public OracleTargetFieldValueResolve(DefaultFieldValueResolver defaultFieldValueResolver) {
        this.defaultFieldValueResolver = defaultFieldValueResolver;
    }


    @Override
    public String writeResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition tfm, Object object) {
        String value = null;
        FieldJavaModel fieldJavaModel = tfm.getTargetFieldModel().getFieldJavaModel();
        FieldJavaType type = fieldJavaModel.getFieldJavaType();
        switch (type) {
            case Time:
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("TO_DATE('%s','hh24:mi:ss')", DateUtil.format((Date) object, "HH:mm:ss"));
                    }
                    return null;
                });
                break;
            case Date:
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("TO_DATE('%s','yyyy-mm-dd')", DateUtil.format((Date) object, "yyyy-MM-dd"));
                    }

                    return null;
                });
                break;
            case DateTime:
            case Timestamp:
                // 系统会自动处理null的default的值
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("TO_DATE('%s','yyyy-mm-dd hh24:mi:ss')", DateUtil.format((Date) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (object instanceof LocalDateTime) {
                        return String.format("TO_DATE('%s','yyyy-mm-dd hh24:mi:ss')", DateUtil.format((LocalDateTime) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (object instanceof Timestamp) {
                        return String.format("TO_DATE('%s.000000','yyyy-mm-dd hh24:mi:ss.ff')", DateUtil.format((Timestamp) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    return null;
                });
                break;
        }
        if (value == null) {
            value = defaultFieldValueResolver.writeResolve(dbTransferType, tvm, tfm, object);
        }
        return value;
    }

    @Override
    public Object execResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition definition, Object object) {
        return object;
    }
}
