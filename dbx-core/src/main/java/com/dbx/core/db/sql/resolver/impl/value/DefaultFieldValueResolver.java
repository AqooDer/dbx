package com.dbx.core.db.sql.resolver.impl.value;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.db.DbTransferType;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.sql.resolver.FieldValueResolver;
import com.dbx.core.exception.JobExecuteException;
import lombok.extern.slf4j.Slf4j;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;

/**
 * @author Aqoo
 */
@Slf4j
public class DefaultFieldValueResolver implements FieldValueResolver {

    @Override
    public String writeResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition definition, Object object) {
        String value;
        FieldJavaModel fieldJavaModel = definition.getTargetFieldModel().getFieldJavaModel();
        FieldJavaType type = fieldJavaModel.getFieldJavaType();
        switch (type) {
            case String:
            case FixedString:
                value = get(object, () -> null, () -> {
                    if (object instanceof String) {
                        // 支持 MYSQL postgre SQLSERVER ORACLE
                        return String.format("'%s'", ((String) object).replace("'", "''")); // 转译sql语句
                    } else {
                        return String.format("'%s'", object.toString());
                    }
                });
                break;
            case Time:
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("'%s'", DateUtil.format((Date) object, "HH:mm:ss"));
                    }
                    return null;
                });
                break;
            case Date:
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("'%s'", DateUtil.format((Date) object, "yyyy-MM-dd"));
                    }

                    return null;
                });
                break;
            case DateTime:
            case Timestamp:
                // 系统会自动处理null的default的值
                value = get(object, () -> null, () -> {
                    if (object instanceof Date) {
                        return String.format("'%s'", DateUtil.format((Date) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (object instanceof LocalDateTime) {
                        return String.format("'%s'", DateUtil.format((LocalDateTime) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (object instanceof Timestamp) {
                        return String.format("'%s'", DateUtil.format((Timestamp) object, "yyyy-MM-dd HH:mm:ss"));
                    }
                    return null;
                });
                break;
            case Short:
            case Integer:
            case Long:
                value = get(object, () -> null, () -> Long.decode(object.toString()).toString());
                break;
            case Double:
                value = get(object, () -> null, () -> Double.valueOf(object.toString()).toString());
                break;
            case Decimal:
                value = get(object, () -> null, () -> decimal(fieldJavaModel.getLength(), fieldJavaModel.getDecimalDigits(), object));
                break;
            case Bytes:
            case FixedBytes:
                // 默认转成 base64的字符 比如可能存储的图片等
                value = get(object, () -> null, () -> Base64.getEncoder().encodeToString((byte[]) object));
                break;
            default:
                throw new JobExecuteException(String.format(" can not deal with the field '%s' value ",
                        definition.getTargetField()));
        }
        if (object != null && value == null) {
            log.warn("the object parse error , please check. object class:{}", object.getClass());
        }
        return value;
    }

    @Override
    public Object execResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition definition, Object object) {
        return object;
    }

    /**
     * NumberUtil.decimalFormat("00.000", object)
     *
     * @param length
     * @param decimalDigits
     * @param value
     * @return
     */
    private static String decimal(Integer length, Integer decimalDigits, Object value) {
        int len = length == null ? 10 : length;
        int scale = decimalDigits == null ? 2 : decimalDigits;
        StringBuilder format = new StringBuilder();
        for (int i = 0; i < len - 2; i++) {
            format.append("0");
        }
        format.append(".");
        for (int i = 0; i < scale; i++) {
            format.append("0");
        }
        return NumberUtil.decimalFormat(format.toString(), value);
    }

}
