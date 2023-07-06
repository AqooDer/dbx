package com.dbx.bean.config.resolve.merge;

import com.dbx.bean.config.resolve.Meson;
import com.dbx.bean.config.resolve.definition.AnnotationValueFormatDefinition;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.resolve.definition.AnnotationTableFieldMapperDefinition;
import com.dbx.bean.config.resolve.definition.AnnotationTableRowValueMapperDefinition;
import com.dbx.bean.config.support.ValueDefaultType;
import com.dbx.core.config.FieldValueFormatDefinition;
import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDefinitionException;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;

/**
 * @author Aqoo
 */
public class ValueResolver {

    /**
     * 值的设置：value设置关系 ： customFormatValue > defaultFormatValue > defaultValue > ref >  SOURCE
     *
     * @return TableValueMapperDefinition
     */
    public TableRowValueMapperDefinition resolve(Meson meson, TableMapperDefinition mapperDefinition) {
        List<MapperField> mapperFields = meson.getMapperFields();
        Set<String> includeFields = meson.getIncludeFields();
        Set<String> excludeFields = meson.getExcludeFields();
        TableModel sourceTableModel = meson.getSourceTableModel();
        Map<String, FieldModel> sourceFieldModel = sourceTableModel == null ? new HashMap<>(8) : sourceTableModel.getFieldModels();

        AnnotationTableRowValueMapperDefinition definition = new AnnotationTableRowValueMapperDefinition(meson, mapperDefinition);

        Map<String, FieldModel> fieldModels = mapperDefinition.getTableModel().getFieldModels();
        Map<String, TableFieldValueMapperDefinition> fieldValueMapperDefinitions = new HashMap<>();

        Function<String, AnnotationTableFieldMapperDefinition> fun = k -> AnnotationTableFieldMapperDefinition.newSingleInstance
                (mapperDefinition.getTableModel(), k, fieldModels.get(k), AnnotationValueFormatDefinition.newSource(k));

        sourceFieldModel.forEach((k, v) -> {
            if (includeFields.isEmpty() && excludeFields.isEmpty()) {
                // 找不到ddl定义的不要。
                if (fieldModels.get(k) != null) {
                    fieldValueMapperDefinitions.put(k, fun.apply(k));
                }
            }
            if (!includeFields.isEmpty()) {
                if (includeFields.contains(k) && fieldModels.get(k) != null) {
                    fieldValueMapperDefinitions.put(k, fun.apply(k));
                }
            }
            if (!excludeFields.isEmpty()) {
                if (!excludeFields.contains(k) && fieldModels.get(k) != null) {
                    fieldValueMapperDefinitions.put(k, fun.apply(k));
                }
            }
        });

        // 放在下面 ，如果定义重复下面的会覆盖上面
        for (MapperField mapperField : mapperFields) {
            FieldModel fieldModel = fieldModels.get(mapperField.target());
            if (fieldModel == null) {
                throw new JobDefinitionException(String.format("the field '%s %s' is not found the ddl definition,please check.",
                        mapperDefinition.getTableModel().getTableName(), mapperField.target()));
            }

            AnnotationTableFieldMapperDefinition valueMapperDefinition = new AnnotationTableFieldMapperDefinition();
            List<FieldValueFormatDefinition> valueFormatDefinition = new ArrayList<>();
            if (!mapperField.defaultFormatValue().equals(ValueDefaultType.NONE)) {
                valueFormatDefinition.add(AnnotationValueFormatDefinition.newValueDefaultType(mapperField.defaultFormatValue()));
            }
            if (StringUtils.hasText(mapperField.defaultValue())) {
                valueFormatDefinition.add(AnnotationValueFormatDefinition.newDefaultValue(mapperField.defaultValue()));
            }
            if (StringUtils.hasText(mapperField.superTarget())) {
                valueFormatDefinition.add(AnnotationValueFormatDefinition.newSuperTarget(mapperField.superTarget()));
            }
            if (StringUtils.hasText(mapperField.superSource())) {
                valueFormatDefinition.add(AnnotationValueFormatDefinition.newSuperSource(mapperField.superSource()));
            }
            if (StringUtils.hasText(mapperField.source())) {
                valueFormatDefinition.add(AnnotationValueFormatDefinition.newSource(mapperField.source()));
            }
            valueFormatDefinition.add(AnnotationValueFormatDefinition.newValueDefaultType(mapperField.defaultFormatValue()));
            valueMapperDefinition.setTableModel(definition.getTableMapperDefinition().getTableModel());
            valueMapperDefinition.setValueFormatDefinition(valueFormatDefinition);
            valueMapperDefinition.setTargetField(mapperField.target());
            valueMapperDefinition.setFieldModel(fieldModel);
            fieldValueMapperDefinitions.put(mapperField.target(), valueMapperDefinition);
        }
        definition.setFieldValueMapperDefinition(new ArrayList<>(fieldValueMapperDefinitions.values()));
        return definition;
    }
}
