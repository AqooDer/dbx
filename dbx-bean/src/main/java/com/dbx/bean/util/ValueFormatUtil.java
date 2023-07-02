package com.dbx.bean.util;

import com.dbx.bean.config.support.ValueDefaultType;
import com.dbx.core.util.select.Matcher;
import com.dbx.core.util.select.Selector;

import java.util.UUID;

/**
 * @author Aqoo
 */
public class ValueFormatUtil {
    public static Object valueFormat(ValueDefaultType valueDefaultType) {
        return new Selector<>(valueDefaultType)
                .match(Matcher.of((v -> valueDefaultType == ValueDefaultType.NONE), (v -> null)))
                .match(Matcher.of((v -> valueDefaultType == ValueDefaultType.UUID), (v -> generateUuid())))
                .match(Matcher.of((v -> valueDefaultType == ValueDefaultType.UUID_NO_LINE), (v -> generateUuidNoLine())))
                .end();
    }

    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    public static String generateUuidNoLine() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
