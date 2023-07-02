package com.dbx.core.constans;

/**
 * <a href="https://www.likecs.com/show-783486.html">类型介绍</a>
 * <p>
 * 为了简化
 *
 * @author Aqoo
 */

public enum FieldJavaType {
    /**
     * 程序设计需要，无对应关系
     */
    NONE,
    /**
     * 不同的数据库根据长度去判断,一般使用VARCHAR就行了
     * CHAR  254个字符
     * CLOB
     * LONGNVARCHAR
     * LONGVARCHAR
     * NCHAR
     * NCLOB
     * NVARCHAR
     * VARCHAR
     */
    String,
    /**
     * 固定长度的String
     */
    FixedString,
    /**
     * 二进制 BINARY VARBINARY 或 LONGVARBINARY
     */
    Bytes,

    FixedBytes,

    Date,
    DateTime,
    Time,
    Timestamp,
    /**
     * 长度：
     * TINYINT (0,255) == Byte, 简化类型
     * length=(0,4]  Short  db：SMALLINT  16位的有符号整数，其值在 -32768 和 32767 之间
     * length=(4,8]  Integer  db： INTEGER
     * length=(9,18]  Long  db:long
     * length=(18,]  BigDecimal db:NUMERIC，DECIMAL
     */
    Short,
    Integer,
    Long,
    /**
     * ||Decimal
     * <p>
     * REAL
     */
    Decimal,
    /**
     * Float||Double
     */
    Double,
}
