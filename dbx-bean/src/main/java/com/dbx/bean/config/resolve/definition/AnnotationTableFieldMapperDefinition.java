package com.dbx.bean.config.resolve.definition;

import com.dbx.core.config.FieldValueFormatDefinition;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Aqoo
 */
@Setter
@ToString()
public class AnnotationTableFieldMapperDefinition implements TableFieldValueMapperDefinition {
    @NonNull
    private TableModel tableModel;

    @NonNull
    private String targetField;

    @NonNull
    private FieldModel fieldModel;

    @NonNull
    private List<FieldValueFormatDefinition> valueFormatDefinition;

    @Override
    public @NonNull String getTargetField() {
        return targetField;
    }

    @Override
    public FieldModel getTargetFieldModel() {
        return fieldModel;
    }

    @Override
    public @NonNull List<FieldValueFormatDefinition> getValueFormatDefinition() {
        return valueFormatDefinition;
    }

    public static AnnotationTableFieldMapperDefinition newSingleInstance(TableModel tableModel, String targetField, FieldModel fieldModel,
                                                                         FieldValueFormatDefinition fieldValueFormatDefinition) {
        AnnotationTableFieldMapperDefinition annotationFieldMapperDefinition = new AnnotationTableFieldMapperDefinition();
        annotationFieldMapperDefinition.tableModel = tableModel;
        annotationFieldMapperDefinition.targetField = targetField;
        annotationFieldMapperDefinition.valueFormatDefinition = new ArrayList<>();
        annotationFieldMapperDefinition.fieldModel = fieldModel;
        annotationFieldMapperDefinition.valueFormatDefinition.add(fieldValueFormatDefinition);
        return annotationFieldMapperDefinition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AnnotationTableFieldMapperDefinition that = (AnnotationTableFieldMapperDefinition) o;
        return this.targetField.equals(that.getTargetField());
    }
}
