package com.dbx.bean.util;

import com.dbx.bean.config.annotation.UseDdl;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Aqoo
 */
public class AnnotationUtil {
    public static MapperTable getMapperTable(Class<?> clazz) {
        return getAnnotation(clazz, MapperTable.class);
    }

    public static List<MapperField> getMapperFields(Class<?> clazz) {
        List<MapperField> list = new ArrayList<>();
        if (clazz != null) {
            MapperField[] annotationsByType = clazz.getAnnotationsByType(MapperField.class);
            if (annotationsByType.length > 0) {
                list.addAll(Arrays.asList(annotationsByType));
            }
            list.addAll(getMapperFields(clazz.getSuperclass()));
        }
        return list;
    }

    public static UseDdl getUseDdl(Class<?> clazz) {
        return getAnnotation(clazz, UseDdl.class);
    }


    private static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationClass) {
        if (clazz != null) {
            A annotation = clazz.getAnnotation(annotationClass);
            if (annotation == null) {
                annotation = getAnnotation(clazz.getSuperclass(), annotationClass);
            }
            return annotation;
        }
        return null;
    }

}
