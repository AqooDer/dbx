package com.dbx.bean.config.resolve.merge;

import cn.hutool.core.collection.CollUtil;
import com.dbx.bean.config.annotation.DdlConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.bean.config.resolve.Meson;
import com.dbx.bean.config.resolve.definition.AnnotationTableMapperDefinition;
import com.dbx.core.constans.FieldJavaType;
import com.dbx.core.constans.FieldType;
import com.dbx.core.db.datasource.model.FieldDbModel;
import com.dbx.core.db.datasource.model.FieldJavaModel;
import com.dbx.core.db.datasource.model.FieldModel;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDefinitionException;
import com.dbx.core.job.JobTool;
import com.dbx.core.util.jdbc.DbUtil;
import com.dbx.core.util.select.Matcher;
import com.dbx.core.util.select.Selector;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * copy 原表信息
 *
 * @author Aqoo
 */
public class SchemaResolver {

    private final JobTool jobTool;


    public SchemaResolver(JobTool jobTool) {
        this.jobTool = jobTool;
    }

    /**
     * @param meson     该类定义的class类
     * @param parentTmd 该表关联的parent属性定义
     * @throws JobDefinitionException 解析异常
     */
    public TableModel getTableModel(Meson meson, AnnotationTableMapperDefinition parentTmd) throws JobDefinitionException {
        Set<String> includeFields = meson.getIncludeFields();
        Set<String> excludeFields = meson.getExcludeFields();
        Map<String, MapperField> mapperFieldMap = meson.getMapperFieldMap();

        TableModel sourceTableModel = meson.getSourceTableModel();
        Map<String, FieldModel> sourceFieldModelMap = sourceTableModel == null ? new HashMap<>(1) : sourceTableModel.getFieldModels();

        TableModel tableModel = initTableModel(meson, sourceTableModel);
        Map<String, FieldModel> fieldModelMap = new HashMap<>(60);

        // 解析 mapperFields
        // ddl设置关系： ddl > ref > SOURCE db数据库转换
        // 值的设置：value设置关系 ： customFormatValue > defaultFormatValue > defaultValue > ref >  SOURCE

        mapperFieldMap.forEach((k, v) -> {
            k = k.toLowerCase(Locale.ROOT);
            String source = v.source().toLowerCase();
            if (!fieldModelMap.containsKey(k)) {
                FieldJavaModel fieldJavaModel = null;
                FieldModel sourceFieldModel = null;
                FieldType sourceFrom = FieldType.SOURCE;
                FieldModel targetFieldModel = null;
                if (v.ddl().type() != FieldJavaType.NONE) {
                    if (v.id()) {
                        throw new JobDefinitionException(String.format("the 'ddl' and the 'id' properties cannot exist at the same time in class '%s'",
                                meson.getConfig().getName()));
                    }
                    fieldJavaModel = generateJavaModel(k, v.ddl());
                    targetFieldModel = getFieldModelFromDdl(fieldJavaModel);
                }
                if (targetFieldModel == null && v.id()) {
                    fieldJavaModel = DbUtil.getDefaultIdFieldJavaModel(k);
                    targetFieldModel = getFieldModelFromDdl(fieldJavaModel);
                }
                if (targetFieldModel == null && StringUtils.hasText(v.superTarget())) {
                    // 该地方应该是没得类型转换的问题
                    Map<String, FieldModel> parentTargetMap = parentTmd.getTableModel().getFieldModels();
                    sourceFieldModel = parentTargetMap.get(v.superTarget().toLowerCase());
                    sourceFrom = FieldType.SUPER_TARGET;
                    targetFieldModel = getFieldModelFromSuper(k, sourceFieldModel);
                }

                // 当前表的原表定义处理
                if (StringUtils.hasText(v.superSource())) {
                    if (parentTmd == null) {
                        throw new JobDefinitionException(String.format("the ddl definition of field '%s' in table '%s' is error, the SUPER_SOURCE is '%s'.the class is '%s'",
                                v.target(),
                                meson.getMapperTable().target(), v.superSource(), meson.getConfig().getName()));
                    }
                    if (parentTmd.getSourceTableModel() != null) {
                        // 这样写是为了给 ddl定义和id定时时赋值。
                        sourceFieldModel = parentTmd.getSourceTableModel().getFieldModels().get(v.superSource().toLowerCase());
                        sourceFrom = FieldType.SUPER_SOURCE;
                        if (targetFieldModel == null && sourceFieldModel != null) {
                            targetFieldModel = getFieldModelFromSuper(k, sourceFieldModel);
                        }
                    }
                }

                if (StringUtils.hasText(source)) {
                    sourceFieldModel = sourceFieldModelMap.get(source);
                    if (sourceFieldModel == null) {
                        throw new JobDefinitionException(String.format("the sourceFieldModel '%s' in ddl definition of field '%s' in table '%s' is not found.the class is '%s'",
                                v.source(), v.target(),
                                meson.getMapperTable().target(), meson.getConfig().getName()));
                    }
                    sourceFrom = FieldType.SOURCE;
                    if (targetFieldModel == null) {
                        targetFieldModel = getFieldModelFromOwn(k, sourceFieldModel);
                    }
                }

                if (targetFieldModel != null) {
                    if (targetFieldModel.getSource() == null) {
                        targetFieldModel.setSource(sourceFieldModel);
                        targetFieldModel.setSourceFrom(sourceFrom);
                    }
                    fieldModelMap.put(k, targetFieldModel);
                    return;
                }
                throw new JobDefinitionException(String.format("the ddl definition of field '%s' in table '%s' is not found.the class is '%s'", v.target(),
                        meson.getMapperTable().target(), meson.getConfig().getName()));
            }
        });

        // 解析 mapperTable 的 includeFields ，excludeFields
        sourceFieldModelMap.forEach((k, v) -> {
            k = k.toLowerCase(Locale.ROOT);
            if (!fieldModelMap.containsKey(k)) {
                if (includeFields.isEmpty() && excludeFields.isEmpty()) {
                    fieldModelMap.put(k, getFieldModelFromOwn(k, v));
                }
                if (!includeFields.isEmpty() && includeFields.contains(k)) {
                    fieldModelMap.put(k, getFieldModelFromOwn(k, v));
                }
                if (!excludeFields.isEmpty() && !excludeFields.contains(k)) {
                    fieldModelMap.put(k, getFieldModelFromOwn(k, v));
                }
            }
        });

        doConfirmId(fieldModelMap, mapperFieldMap, meson.getConfig().getName());
        tableModel.setFieldModels(fieldModelMap);
        return tableModel;
    }


    private FieldModel getFieldModelFromSuper(String fieldName, FieldModel sourceFieldModel) {
        FieldJavaModel javaModel = sourceFieldModel.getFieldJavaModel().copy(fieldName);
        FieldDbModel fieldDbModel = jobTool.getTargetFieldTypeResolver().javaModel2DbModel(javaModel);
        return FieldModel.builder().fieldJavaModel(javaModel).fieldDbModel(fieldDbModel).build();
    }

    private FieldModel getFieldModelFromDdl(FieldJavaModel fieldJavaModel) {
        FieldDbModel fieldDbModel = jobTool.getTargetFieldTypeResolver().javaModel2DbModel(fieldJavaModel);
        return FieldModel.builder().fieldJavaModel(fieldJavaModel).fieldDbModel(fieldDbModel).build();
    }

    private FieldModel getFieldModelFromOwn(String fileName, FieldModel sourceFieldModel) {
        FieldJavaModel fromFieldJavaModel = sourceFieldModel.getFieldJavaModel();
        FieldJavaModel toFieldJavaModel = fromFieldJavaModel.copy(fileName);
        FieldDbModel fieldDbModel = jobTool.getTargetFieldTypeResolver().javaModel2DbModel(fromFieldJavaModel);
        // 转换后，替换列名
        toFieldJavaModel.setFieldName(fileName);
        fieldDbModel.setFieldName(fileName);
        return FieldModel.builder().fieldJavaModel(toFieldJavaModel).fieldDbModel(fieldDbModel).source(sourceFieldModel).sourceFrom(FieldType.SOURCE).build();
    }

    private FieldModel getFieldModel(String fileName, FieldJavaModel fieldJavaModel, FieldModel sourceFieldModel) {
        FieldDbModel fieldDbModel = jobTool.getTargetFieldTypeResolver().javaModel2DbModel(fieldJavaModel);
        FieldModel model = FieldModel.builder().fieldJavaModel(fieldJavaModel).fieldDbModel(fieldDbModel).source(sourceFieldModel).build();
        // 转换后，替换列名
        model.getFieldDbModel().setFieldName(fileName);
        model.getFieldJavaModel().setFieldName(fileName);
        return model;
    }

    private TableModel initTableModel(Meson meson, TableModel sourceTableModel) {
        MapperTable mapperTable = meson.getMapperTable();
        TableModel tableModel = new TableModel();
        tableModel.setTableName(meson.getMapperTable().target());
        tableModel.setContent(mapperTable.content());
        if (!StringUtils.hasText(tableModel.getContent()) && sourceTableModel != null) {
            tableModel.setContent(sourceTableModel.getContent());
        }
        tableModel.setSource(sourceTableModel);
        return tableModel;
    }

    /**
     * 合并去重复的id值
     *
     * @param fieldModelMap  已经生成的field配置
     * @param mapperFieldMap 原始mapperFieldMap配置
     */
    private void doConfirmId(Map<String, FieldModel> fieldModelMap, Map<String, MapperField> mapperFieldMap, String className) {
        Set<FieldDbModel> fieldDbModels = fieldModelMap.values().stream().map(FieldModel::getFieldDbModel).filter(FieldDbModel::getPk).collect(Collectors.toSet());
        if (fieldDbModels.isEmpty()) {
            throw new JobDefinitionException(String.format("the '%s' mapper definition not found the id definition , please check.", className));
        }
        if (CollUtil.isNotEmpty(mapperFieldMap)) {
            FieldDbModel idSelected = new Selector<Set<FieldDbModel>, FieldDbModel>(fieldDbModels)
                    .match(new IdMatcher(mapperFieldMap, MapperField::id))
                    .match(new IdMatcher(mapperFieldMap, mf -> mf.ddl().type() != FieldJavaType.NONE))
                    .match(new IdMatcher(mapperFieldMap, mf -> StringUtils.hasText(mf.superTarget())))
                    .match(new IdMatcher(mapperFieldMap, mf -> StringUtils.hasText(mf.superSource())))
                    .match(new IdMatcher(mapperFieldMap, mf -> StringUtils.hasText(mf.source()))).end();
            // 这里忽略了 mapperTable原表的引用，因为上诉优先级只有要一个满足，mapperTable的引用都会失效，而如果只是引用原表，原表也只会有一个id。
            if (idSelected != null) {
                fieldDbModels.forEach(model -> {
                    if (!model.getFieldName().equals(idSelected.getFieldName())) {
                        model.setPk(false);
                    }
                });
            }
        }
    }

    private FieldJavaModel generateJavaModel(String fieldName, DdlConfig config) {
        FieldJavaModel fieldJavaModel = new FieldJavaModel();
        fieldJavaModel.setFieldName(fieldName);
        fieldJavaModel.setPk(config.primary());
        fieldJavaModel.setFieldJavaType(config.type());
        fieldJavaModel.setContent(config.content());
        fieldJavaModel.setNullable(config.nullable());
        fieldJavaModel.setLength(config.length());
        fieldJavaModel.setDecimalDigits(config.scale());
        fieldJavaModel.setDefaultValue(config.defaultValue());
        return fieldJavaModel;
    }

    static class IdMatcher implements Matcher<Set<FieldDbModel>, FieldDbModel> {
        private FieldDbModel hit;
        private final Map<String, MapperField> mapperFieldMap;
        private final Predicate<MapperField> judgment;

        public IdMatcher(Map<String, MapperField> mapperFieldMap, Predicate<MapperField> judgment) {
            this.judgment = judgment;
            this.mapperFieldMap = mapperFieldMap;
        }

        @Override
        public Predicate<Set<FieldDbModel>> match() {
            return infos -> infos.stream().anyMatch(model -> {
                MapperField mapperField = mapperFieldMap.get(model.getFieldName());
                return mapperField != null && new Selector<MapperField, Boolean>(mapperField).match(Matcher.of(judgment, mf -> {
                    hit = model;
                    return Boolean.TRUE;
                })).orElse(mf -> Boolean.FALSE);
            });
        }

        @Override
        public Function<Set<FieldDbModel>, FieldDbModel> executor() {
            return (infos) -> hit;
        }
    }
}
