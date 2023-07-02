/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.dbx.core.constans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;

/**
 * 表字段类型
 *
 * @author Aqoo
 * @since 2017-01-11
 */
public enum JavaFieldType {

    // 基本类型
    BASE_BYTE("byte", null, byte.class),
    BASE_BYTE_ARRAY("byte[]", null, byte[].class),
    BASE_SHORT("short", null, short.class),
    BASE_CHAR("char", null, char.class),
    BASE_INT("int", null, int.class),
    BASE_LONG("long", null, long.class),
    BASE_FLOAT("float", null, float.class),
    BASE_DOUBLE("double", null, double.class),
    BASE_BOOLEAN("boolean", null, boolean.class),

    // 包装类型
    BYTE("Byte", null, Byte.class),
    BYTE_ARRAY("Byte[]", null, Byte[].class),
    SHORT("Short", null, Short.class),
    CHARACTER("Character", null, Character.class),
    INTEGER("Integer", null, Integer.class),
    LONG("Long", null, Long.class),
    FLOAT("Float", null, Float.class),
    DOUBLE("Double", null, Double.class),
    BOOLEAN("Boolean", null, Boolean.class),
    STRING("String", null, String.class),

    // sql 包下数据类型
    DATE_SQL("Date", "java.sql.Date", Date.class),
    TIME("Time", "java.sql.Time", Time.class),
    TIMESTAMP("Timestamp", "java.sql.Timestamp", Timestamp.class),
    BLOB("Blob", "java.sql.Blob", Blob.class),
    CLOB("Clob", "java.sql.Clob", Clob.class),

    // java8 新时间类型
    LOCAL_DATE("LocalDate", "java.time.LocalDate", LocalDate.class),
    LOCAL_TIME("LocalTime", "java.time.LocalTime", LocalTime.class),
    YEAR("Year", "java.time.Year", Year.class),
    YEAR_MONTH("YearMonth", "java.time.YearMonth", YearMonth.class),
    LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime", LocalDateTime.class),
    INSTANT("Instant", "java.time.Instant", Instant.class),

    // 其他杂类
    OBJECT("Object", null, Object.class),
    DATE("Date", "java.util.Date", java.util.Date.class),
    BIG_INTEGER("BigInteger", "java.math.BigInteger", BigInteger.class),
    BIG_DECIMAL("BigDecimal", "java.math.BigDecimal", BigDecimal.class);

    /**
     * 类型
     */
    private final String type;
    /**
     * 类类型
     */
    private final Class<?> cls;
    /**
     * 包路径
     */
    private final String pkg;

    JavaFieldType(final String type, final String pkg, final Class<?> cls) {
        this.type = type;
        this.pkg = pkg;
        this.cls = cls;
    }

    public String getType() {
        return type;
    }

    public String getPkg() {
        return pkg;
    }

    public Class<?> getCls() {
        return cls;
    }

    public static JavaFieldType getBy(Class<?> cls) {
        for (JavaFieldType type : JavaFieldType.values()) {
            if (type.cls == cls) {
                return type;
            }
        }
        return null;
    }

    public static JavaFieldType[] getDateTimeTypes() {
        return new JavaFieldType[]{LOCAL_DATE_TIME, DATE, DATE_SQL, TIMESTAMP, LOCAL_DATE, LOCAL_TIME, TIME};
    }
}
