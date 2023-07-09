package com.dbx.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Aqoo
 */
public class RegexUtil {

    public static final Pattern ORACLE_NAME_REG = Pattern.compile("([a-zA-z])([a-zA-z0-9_#$]*)");

    public static boolean match(Pattern pattern, String... value) {
        Matcher matcher;
        for (String v : value) {
            matcher = pattern.matcher(v);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }


    public static void findTest(Pattern pattern, String... value) {
        Matcher matcher;
        for (String v : value) {
            matcher = pattern.matcher(v);
            if (matcher.find()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    System.out.printf("值：%s 存在数据： %s \n", v, matcher.group(i));
                }
            }
        }
    }

    public static void matchTest(Pattern pattern, String... value) {
        Matcher matcher;
        for (String v : value) {
            matcher = pattern.matcher(v);
            System.out.printf("值：%s %s匹配 规则：%s%n", v, matcher.matches() ? "" : "不", pattern);
        }
    }


}
