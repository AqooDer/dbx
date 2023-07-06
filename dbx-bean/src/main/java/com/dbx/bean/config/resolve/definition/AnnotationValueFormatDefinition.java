package com.dbx.bean.config.resolve.definition;

import com.dbx.bean.config.support.ValueDefaultType;
import com.dbx.core.config.FieldValueFormatDefinition;
import com.dbx.core.exception.JobDefinitionException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Aqoo
 * 值的设置：value设置关系 ： customFormatValue > defaultFormatValue > defaultValue > ref >  SOURCE
 */
@Setter
@Getter
@ToString()
public class AnnotationValueFormatDefinition implements FieldValueFormatDefinition {
    /**
     * customFormatValue:1
     * defaultFormatValue :2
     * defaultValue:3
     * SUPER_TARGET : 4
     * SUPER_SOURCE : 5
     * SOURCE : 6
     */
    private int type;

    private Object format;

    private AnnotationValueFormatDefinition next;


    public static AnnotationValueFormatDefinition newValueDefaultType(ValueDefaultType valueDefaultType) {
        AnnotationValueFormatDefinition annotationValueFormatDefinition = new AnnotationValueFormatDefinition();
        annotationValueFormatDefinition.setType(2);
        annotationValueFormatDefinition.setFormat(valueDefaultType);
        return annotationValueFormatDefinition;
    }

    public static AnnotationValueFormatDefinition newDefaultValue(String defaultValue) {
        AnnotationValueFormatDefinition annotationValueFormatDefinition = new AnnotationValueFormatDefinition();
        annotationValueFormatDefinition.setType(3);
        annotationValueFormatDefinition.setFormat(defaultValue);
        return annotationValueFormatDefinition;
    }

    public static AnnotationValueFormatDefinition newSuperTarget(String ref) {
        AnnotationValueFormatDefinition annotationValueFormatDefinition = new AnnotationValueFormatDefinition();
        annotationValueFormatDefinition.setType(4);
        annotationValueFormatDefinition.setFormat(ref);
        return annotationValueFormatDefinition;
    }

    public static AnnotationValueFormatDefinition newSuperSource(String ref) {
        AnnotationValueFormatDefinition annotationValueFormatDefinition = new AnnotationValueFormatDefinition();
        annotationValueFormatDefinition.setType(5);
        annotationValueFormatDefinition.setFormat(ref);
        return annotationValueFormatDefinition;
    }


    public static AnnotationValueFormatDefinition newSource(String source) {
        AnnotationValueFormatDefinition annotationValueFormatDefinition = new AnnotationValueFormatDefinition();
        annotationValueFormatDefinition.setType(6);
        annotationValueFormatDefinition.setFormat(source);
        return annotationValueFormatDefinition;
    }

    @Override
    public Object getValueFormat() throws JobDefinitionException {
        return format;
    }


}
