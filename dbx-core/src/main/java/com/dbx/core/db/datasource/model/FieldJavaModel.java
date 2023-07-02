package com.dbx.core.db.datasource.model;

import com.dbx.core.constans.FieldJavaType;
import lombok.Data;

/**
 * java 内存模型字段
 *
 * @author Aqoo
 */
@Data
public class FieldJavaModel {

    private String fieldName;
    /**
     * java类型
     */
    private FieldJavaType fieldJavaType;

    private String content;

    /**
     * 是否可为空，false 不可为空，true 可以为空
     */
    private Boolean nullable;

    /**
     * 是否主键 true 是主键
     */
    private Boolean pk;

    /**
     * 列大小,长度
     */
    private Integer length;
    /**
     * 小数位数
     */
    private Integer decimalDigits;
    /**
     * 默认值
     */
    private String defaultValue;


    //private boolean fixedLength;


    public FieldJavaModel copy(String fileName) {
        FieldJavaModel fieldJavaModel = new FieldJavaModel();
        fieldJavaModel.setFieldName(fileName);
        fieldJavaModel.setFieldJavaType(this.fieldJavaType);
        fieldJavaModel.setContent(this.content);
        fieldJavaModel.setNullable(this.nullable);
        fieldJavaModel.setPk(this.pk);
        fieldJavaModel.setLength(this.length);
        fieldJavaModel.setDecimalDigits(this.decimalDigits);
        fieldJavaModel.setDefaultValue(this.defaultValue);
        //fieldJavaModel.setFixedLength(this.fixedLength);
        return fieldJavaModel;
    }

}
