package com.dbx.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author Aqoo
 */
public class JsonUtil {
    public static String object2String(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
