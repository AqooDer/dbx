package com.dbx.core.constans;

import java.util.Arrays;

/**
 * @author Aqoo
 */

public enum MysqlFieldType {
    // 数值类型
    TINYINT("N"), SMALLINT("N"), MEDIUMINT("N"), INT("N"), BIGINT("N"), INTEGER("N"),

    // 双精度型
    FLOAT("F"), DOUBLE("F"), DECIMAL("D"),

    // 日期型
    DATE("D"), TIME("D"), YEAR("D"), DATETIME("D"), TIMESTAMP("D"),

    // 字符型
    CHAR("C"), VARCHAR("C"), TINYBLOB("C"), TINYTEXT("C"), BLOB("C"), TEXT("C"), MEDIUMBLOB("C"), MEDIUMTEXT("C"), LONGBLOB("C"),
    LONGTEXT("C");

    private final String code;

    public String getCode() {
        return code;
    }

    MysqlFieldType(String code) {
        this.code = code;
    }


    public static MysqlFieldType match(String code) {
        return Arrays.stream(MysqlFieldType.values())
                .filter(v -> v.name().equalsIgnoreCase(code)).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "MysqlFieldType{" +
                "code='" + code + '\'' +
                '}';
    }
}
