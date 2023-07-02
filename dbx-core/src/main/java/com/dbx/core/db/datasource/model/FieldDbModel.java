package com.dbx.core.db.datasource.model;

import lombok.Data;


/**
 * 字段模型
 *
 * @author Aqoo
 */
@Data
public class FieldDbModel {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 数据库字段类型，比如 varchar char, int 等
     * java.sql.Types类型名称(列类型名称)
     */
    private String type;
    /**
     * 备注信息
     */
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

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName.toLowerCase();
    }

    public void setType(String type) {
        this.type = type.toLowerCase();
    }

    public FieldDbModel copy(String fileName) {
        FieldDbModel fieldDbModel = new FieldDbModel();
        fieldDbModel.setFieldName(fileName);
        fieldDbModel.setType(this.type);
        fieldDbModel.setNullable(this.nullable);
        fieldDbModel.setContent(this.content);
        fieldDbModel.setPk(this.pk);
        fieldDbModel.setLength(this.length);
        fieldDbModel.setDecimalDigits(this.decimalDigits);
        fieldDbModel.setDefaultValue(this.defaultValue);
        return fieldDbModel;
    }
}
