package com.dbx.core.db.sql.resolver.impl.value;

import com.dbx.core.db.DbTransferType;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.constans.OracleFieldTypeEnum;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.sql.resolver.FieldValueResolver;

/**
 * @author Aqoo
 */
public class OracleSourceFieldValueResolver implements FieldValueResolver {
    private final DefaultFieldValueResolver defaultFieldValueResolver;

    public OracleSourceFieldValueResolver(DefaultFieldValueResolver defaultFieldValueResolver) {
        this.defaultFieldValueResolver = defaultFieldValueResolver;
    }

    @Override
    public String writeResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition trm, TableFieldValueMapperDefinition tfm, Object object) {
        String value = null;
        FieldJavaModel fieldJavaModel = tfm.getTargetFieldModel().getFieldJavaModel();
        FieldJavaType type = fieldJavaModel.getFieldJavaType();
        if (type == FieldJavaType.String || type == FieldJavaType.FixedString) {
            if (object instanceof byte[]) {
                FieldModel source = tfm.getTargetFieldModel().getSource();
                if (source != null && source.getFieldDbModel().getType().contains(OracleFieldTypeEnum.raw.name())) {
                    value = String.format("'%s'", oracleRawToString((byte[]) object));
                }
            }
        }
        if (value == null) {
            value = defaultFieldValueResolver.writeResolve(dbTransferType, trm, tfm, object);
        }
        return value;
    }

    @Override
    public Object execResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition tfm, Object object) {
        FieldJavaModel fieldJavaModel = tfm.getTargetFieldModel().getFieldJavaModel();
        FieldJavaType type = fieldJavaModel.getFieldJavaType();
        if (type == FieldJavaType.String) {
            if (object instanceof byte[]) {
                FieldModel source = tfm.getTargetFieldModel().getSource();
                if (source != null && source.getFieldDbModel().getType().contains(OracleFieldTypeEnum.raw.name())) {
                    return oracleRawToString((byte[]) object);
                }
            }
        }
        return object;
    }


    private static String oracleRawToString(byte[] bytes) {
        StringBuilder raw = new StringBuilder();
        for (byte aByte : bytes) {
            //调用工具类，字节进行解析后，拼接到字符串中
            raw.append(decToHex(aByte));
        }
        return raw.toString();
    }

    /**
     * oracle raw 数据转换成String
     *
     * @param dec
     * @return
     */
    public static String decToHex(int dec) {
        StringBuilder hexadecimalNumber = new StringBuilder();
        int length = 8;
        int[] arrayNumber = new int[length];
        for (int i = 0; i < length; i++) {
            arrayNumber[i] = (dec & (0xF << i * 4)) >>> i * 4;
        }

        for (int i = 1; i >= 0; i--) {
            switch (arrayNumber[i]) {
                case 0:
                    hexadecimalNumber.append("0");
                    break;
                case 10:
                    hexadecimalNumber.append("A");
                    break;
                case 11:
                    hexadecimalNumber.append("B");
                    break;
                case 12:
                    hexadecimalNumber.append("C");
                    break;
                case 13:
                    hexadecimalNumber.append("D");
                    break;
                case 14:
                    hexadecimalNumber.append("E");
                    break;
                case 15:
                    hexadecimalNumber.append("F");
                    break;
                default:
                    hexadecimalNumber.append(Integer.toString(arrayNumber[i]));
            }
        }
        return hexadecimalNumber.toString();
    }
}
