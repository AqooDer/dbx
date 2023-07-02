package com.dbx.bean.config.resolve;

import com.dbx.bean.AnnotationJobDefinition;
import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.MapperField;
import com.dbx.bean.config.annotation.MapperTable;
import com.dbx.bean.util.AnnotationUtil;
import com.dbx.bean.util.MapperUtil;
import com.dbx.core.db.datasource.DataSourceWrapper;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDefinitionException;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用于将使用annotation定义的数据模型转换成标准的MapperDefinition模型儿存在的中间态数据处理集合。
 *
 * @author Aqoo
 */
@Getter
public class Meson {
    private final String mapperDefinitionId;

    private final String parentMapperDefinitionId;

    private boolean isChild;

    private final MapperConfig mapperConfig;

    private final MapperTable mapperTable;

    private final List<MapperField> mapperFields;

    /**
     * 含有source配置的 MapperField配置
     */
    private final Map<String, MapperField> mapperFieldMap;

    private final Set<String> includeFields;

    private final Set<String> excludeFields;

    private TableModel sourceTableModel;

    private final DataSourceWrapper sourceWrapper;

    private final Class<?> config;

    private final Map<Class<?>, Meson> children = new HashMap<>();

    private final AnnotationJobDefinition jobDefinition;

    public Meson(AnnotationJobDefinition jobDefinition, MapperConfig mapperConfig, Class<?> config, String parentMapperDefinitionId) {
        this.jobDefinition = jobDefinition;
        this.config = config;
        this.mapperTable = AnnotationUtil.getMapperTable(config);
        if (mapperTable == null) {
            throw new JobDefinitionException(String.format("the class definition %s cannot get mapper com.dbx.bean.config , please check", config.getName()));
        }
        this.mapperDefinitionId = MapperUtil.getMapperDefinitionId(jobDefinition.getJobId(), mapperTable);
        this.mapperFields = AnnotationUtil.getMapperFields(config);

        this.mapperConfig = mapperConfig;
        this.parentMapperDefinitionId = parentMapperDefinitionId;
        if (parentMapperDefinitionId != null) {
            this.isChild = true;
        }

        this.mapperFieldMap = mapperFields.stream().collect(Collectors.toMap(mapperField -> mapperField.target().toLowerCase(), Function.identity()));
        this.includeFields = Sets.newHashSet(mapperTable.includeFields()).stream().map(String::toLowerCase).collect(Collectors.toSet());
        this.excludeFields = Sets.newHashSet(mapperTable.excludeFields()).stream().map(String::toLowerCase).collect(Collectors.toSet());

        this.sourceWrapper = jobDefinition.getJobTool().getDataSourceMapping().getSourceWrapper();
        // 支持数据 都是默认赋值和生成的方式
        if (StringUtils.hasText(mapperTable.source()) && sourceWrapper != null) {
            sourceTableModel = sourceWrapper.getTableModel(mapperTable.source(),
                    jobDefinition.getJobTool().getSourceFieldTypeResolver(),
                    jobDefinition.getJobTool().getSourceSqlGenerator());
        }
    }

    public void addChild(Meson meson) {
        children.put(meson.getConfig(), meson);
    }
}
