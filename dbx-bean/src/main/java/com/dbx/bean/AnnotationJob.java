package com.dbx.bean;

import com.dbx.bean.config.MapperConfig;
import com.dbx.bean.config.annotation.UseDdl;
import com.dbx.bean.config.resolve.Meson;
import com.dbx.bean.config.resolve.definition.AnnotationTableMapperDefinition;
import com.dbx.bean.config.resolve.definition.AnnotationTableRowValueMapperDefinition;
import com.dbx.bean.config.resolve.merge.SchemaResolver;
import com.dbx.bean.config.resolve.merge.ValueResolver;
import com.dbx.core.config.TableMapperDefinition;
import com.dbx.core.db.datasource.model.TableModel;
import com.dbx.core.exception.JobDefinitionException;
import com.dbx.core.job.AbstractJob;
import com.dbx.core.job.Channel;
import com.dbx.core.job.JobConfig;
import com.dbx.core.job.JobDefinition;
import com.dbx.core.util.select.Matcher;
import com.dbx.core.util.select.Selector;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationJob extends AbstractJob {

    private final List<MapperConfig> mapperConfigs;

    public AnnotationJob(@NonNull String id, @NonNull JobConfig jobConfig,
                         @NonNull List<MapperConfig> mapperConfigs) {
        super(id, jobConfig);
        this.mapperConfigs = mapperConfigs;
    }


    @Override
    public JobDefinition getJobDefinition() {
        if (jobDefinition == null) {
            jobDefinition = new JobDefinitionChannel().transferTo(null);
        }
        return jobDefinition;
    }

    private class JobDefinitionChannel implements Channel<Void, AnnotationJobDefinition> {
        private final Map<String, Map<Class<?>, Meson>> valueMesonMap;
        private final Map<String, Meson> ddlMesonMap;

        public JobDefinitionChannel() {
            this.valueMesonMap = new HashMap<>();
            this.ddlMesonMap = new HashMap<>();
        }

        @Override
        public AnnotationJobDefinition transferTo(Void data) {
            AnnotationJobDefinition jobDefinition = new AnnotationJobDefinition(id, jobConfig);
            resolve(jobDefinition);
            return jobDefinition;
        }

        public void resolve(AnnotationJobDefinition jobDefinition) throws JobDefinitionException {
            // 处理所有root级别的config
            for (MapperConfig mapperConfig : mapperConfigs) {
                Class<?>[] mapperConfigClass = mapperConfig.getMapperConfigs();
                if (mapperConfigClass == null || mapperConfigClass.length == 0) {
                    log.warn("class {} cannot get mapper com.dbx.bean.config,please check.", mapperConfig.getClass().getName());
                    continue;
                }
                // 注入数据库连接器
                // 注册监听器
                // 根据目标表id 合并class
                Arrays.stream(mapperConfigClass).forEach(mapperClass -> doMergeClass(jobDefinition, mapperConfig, mapperClass, null));
            }
            // 数据ddl验证
            doDdlVerifyAndSetDdlConfig();
            // 将definition基础数据信息装入容器中
            doInitDefinition(jobDefinition);
            // 解析ddl数据
            doDdlResolve(jobDefinition);
            // 解析值配置信息
            doValueResolve(jobDefinition);

        }

        private Meson doMergeClass(AnnotationJobDefinition jobDefinition, MapperConfig mapperConfig, Class<?> config, String parentMapperDefinitionId) {
            Meson meson = new Meson(jobDefinition, mapperConfig, config, parentMapperDefinitionId);
            String mapperDefinitionId = meson.getMapperDefinitionId();

            valueMesonMap.computeIfAbsent(mapperDefinitionId, v -> new HashMap<>(16)).put(config, meson);

            Class<?>[] children = meson.getMapperTable().children();
            if (children != null) {
                for (Class<?> child : children) {
                    Meson childMeSon = doMergeClass(jobDefinition, mapperConfig, child, mapperDefinitionId);
                    meson.addChild(childMeSon);
                }
            }
            return meson;
        }

        private void doDdlVerifyAndSetDdlConfig() {
            valueMesonMap.forEach((k, v) -> {
                // 验证 ddl是否重复定义
                if (v.size() == 1) {
                    ddlMesonMap.put(k, v.entrySet().stream().findFirst().get().getValue());
                } else {
                    Set<Class<?>> temp = new HashSet<>();
                    v.forEach((kk, vv) -> {
                        UseDdl annotation = vv.getConfig().getAnnotation(UseDdl.class);
                        if (annotation != null) {
                            temp.add(kk);
                            ddlMesonMap.put(k, vv);
                        }
                    });
                    if (temp.size() != 1) {
                        throw new JobDefinitionException(String.format(" multiple instances can only be specified by one 'UseDdl', %s",
                                v.keySet().stream().map(Class::getName).collect(Collectors.joining(","))));
                    }
                }

                // 验证field 是否重复定义
                v.forEach((kk, vv) -> new Selector<Sets.SetView<String>, Object>(null)
                        .match(new Error(Sets.intersection(vv.getIncludeFields(), vv.getExcludeFields()), "excludeFields", "includeFields")).end());
            });
        }

        /**
         * 初始化 MapperDefinition 并且注册到容器中
         * valueConfigPropertiesMap.size 必然 >= ddlConfigPropertiesMap
         * <p>
         * 情况：当ddl存在于child class上时，root中又有该table的value配置。则 valueConfigPropertiesMap>ddlConfigPropertiesMap
         */
        private void doInitDefinition(AnnotationJobDefinition jobDefinition) {
            ddlMesonMap.forEach((id, meson) -> {
                Set<Class<?>> valueClassSet = valueMesonMap.get(id).values().stream().map(Meson::getConfig).collect(Collectors.toSet());
                AnnotationTableMapperDefinition amd = new AnnotationTableMapperDefinition(meson, valueClassSet, jobDefinition);
                jobDefinition.addMapperDefinition(amd);
            });
        }

        /**
         * 解析单类操作
         * valueConfigPropertiesMap.size 必然 >= ddlConfigPropertiesMap
         *
         * @throws JobDefinitionException MapperDefinitionException
         */
        private void doDdlResolve(AnnotationJobDefinition jobDefinition) throws JobDefinitionException {
            SchemaResolver ddlResolver = new SchemaResolver(jobDefinition.getJobTool());
            for (TableMapperDefinition mapperDefinition : jobDefinition.getAllMapperDefinitions()) {
                AnnotationTableMapperDefinition amd = (AnnotationTableMapperDefinition) mapperDefinition;
                if (amd.getTableModel() != null) {
                    continue;
                }
                // 如果父级别没有处理，先处理parent 。这是由于map排序引起的优先级问题
                AnnotationTableMapperDefinition ptd = (AnnotationTableMapperDefinition) jobDefinition.getMapperDefinition(amd.getParentId());
                if (ptd != null && ptd.getTableModel() == null) {
                    ptd.setTableModel(ddlResolver.getTableModel(ptd.getMeson(), null));
                }
                TableModel tableModel = ddlResolver.getTableModel(amd.getMeson(), ptd);
                amd.setTableModel(tableModel);
            }
        }


        private void doValueResolve(AnnotationJobDefinition jobDefinition) throws JobDefinitionException {
            ValueResolver valueResolver = new ValueResolver();
            valueMesonMap.forEach((id, mesonMap) -> {
                TableMapperDefinition parentMapperDefinition = jobDefinition.getMapperDefinition(id);
                if (parentMapperDefinition == null) {
                    throw new JobDefinitionException(String.format("jobDefinition not exit '%s' instance.", id));
                }
                AnnotationTableMapperDefinition amd = (AnnotationTableMapperDefinition) parentMapperDefinition;
                Set<Class<?>> valueConfig = amd.getValueConfig();
                for (Class<?> aClass : valueConfig) {
                    // 子类的值处理交给 子节点处理
                    Meson meson = mesonMap.get(aClass);
                    if (meson.isChild()) {
                        continue;
                    }
                    AnnotationTableRowValueMapperDefinition parent = (AnnotationTableRowValueMapperDefinition) valueResolver.resolve(meson, parentMapperDefinition);
                    Map<Class<?>, Meson> children = meson.getChildren();
                    if (!children.isEmpty()) {
                        parent.setChild(children.values().stream().map(child -> (AnnotationTableRowValueMapperDefinition) valueResolver.resolve(child,
                                jobDefinition.getMapperDefinition(child.getMapperDefinitionId()))).toArray(AnnotationTableRowValueMapperDefinition[]::new));
                    }
                    amd.addTableValueMapperDefinition(aClass, parent);
                }
            });
        }
    }

    private static class Error implements Matcher<Sets.SetView<String>, Object> {
        private final Sets.SetView<String> set;
        private final String name1;
        private final String name2;

        public Error(Sets.SetView<String> set, String name1, String name2) {
            this.set = set;
            this.name1 = name1;
            this.name2 = name2;
        }

        @Override
        public Predicate<Sets.SetView<String>> match() {
            return (v) -> !set.isEmpty();
        }

        @Override
        public Function<Sets.SetView<String>, Object> executor() {
            return (v) -> {
                throw new JobDefinitionException(String.format("field definition error，%s Fields appear in both %s and %s ", set, name1, name2));
            };
        }
    }

}
