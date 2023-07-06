package com.dbx.bean;

import com.dbx.bean.config.MapperConfig;
import com.dbx.core.db.datasource.DataSourceWrapper;
import com.dbx.core.exception.JobException;
import com.dbx.core.exception.JobDefinitionException;
import com.dbx.core.job.Job;
import com.dbx.core.job.JobConfig;
import com.dbx.core.job.JobFactory;
import com.dbx.core.job.transformer.DataTransformer;
import com.dbx.core.job.transformer.ComparisonSchemaTransformer;
import com.dbx.core.job.transformer.SchemaTransformer;
import com.dbx.core.util.ClassUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class AnnotationJobFactory implements JobFactory {
    private final String id;
    private final String[] args;
    private final JobConfig jobConfig;
    private List<MapperConfig> mapperConfigs;

    private AnnotationJob annotationJob;

    public AnnotationJobFactory(String id, String[] args, JobConfig jobConfig) throws Exception {
        this.id = id;
        this.args = args;
        this.jobConfig = jobConfig;
        init(args);
    }

    public AnnotationJobFactory(String id, String[] args, JobConfig jobConfig, List<Class<? extends MapperConfig>> mapperConfigs) throws Exception {
        this.id = id;
        this.args = args;
        this.jobConfig = jobConfig;
        init(args, mapperConfigs);
    }

    @SafeVarargs
    public AnnotationJobFactory(String id, String[] args, JobConfig jobConfig, Class<? extends MapperConfig>... mapperConfigs) throws Exception {
        this.id = id;
        this.args = args;
        this.jobConfig = jobConfig;
        init(args, mapperConfigs);
    }

    public AnnotationJobFactory(String id, String[] args, JobConfig jobConfig, MapperConfig... mapperConfigs) {
        this.id = id;
        this.args = args;
        this.jobConfig = jobConfig;
        init(args, mapperConfigs);
    }

    @NonNull
    public Job getJob() {
        if (annotationJob == null) {
            annotationJob = new AnnotationJob(id, jobConfig, mapperConfigs);
            AnnotationJobDefinition jobDefinition = (AnnotationJobDefinition) annotationJob.getJobDefinition();
            DataSourceWrapper targetWrapper = jobDefinition.getJobTool().getDataSourceMapping().getTargetWrapper();
            // 添加 mapperDefinition 和 targetDB 之间的schema对比 ， 一般用于mapperDefinition验证
            if (jobConfig.enableTargetSchemaVerify()) {
                if (targetWrapper == null) {
                    log.warn("The TARGET database is not configured for connection, please check the configuration");
                } else {
                    annotationJob.getTransformers().add(new ComparisonSchemaTransformer(jobDefinition));
                }
            }

            // 常见表
            if (jobConfig.enableCreateTable() || jobConfig.enableCreateSchemaScript()) {
                if (jobConfig.enableCreateTable() && jobDefinition.getJobTool().getDataSourceMapping().getTargetWrapper() == null) {
                    if (targetWrapper == null) {
                        throw new JobException("The TARGET database is not configured for connection, please check the configuration");
                    }
                }
                annotationJob.getTransformers().add(new SchemaTransformer(jobDefinition) {

                });
            }


            if (jobConfig.enableInsertData() || jobConfig.enableCreateDataScript()) {
                if (jobConfig.enableInsertData() && jobDefinition.getJobTool().getDataSourceMapping().getTargetWrapper() == null) {
                    if (targetWrapper == null) {
                        throw new JobException("The TARGET database is not configured for connection, please check the configuration");
                    }
                }
                annotationJob.getTransformers().add(new DataTransformer(jobDefinition) {
                });
            }
            // 顺序不要错了，先执行 Schema ，在执行data 。
            annotationJob.initialized();
        }
        return annotationJob;
    }

    /**
     * 扫描main方法包下面的所有继承MapperConfig接口的class类。
     *
     * @throws Exception 抛出执行中的异常
     * @see MapperConfig
     */
    @SuppressWarnings("unchecked")
    private void init(String[] args) throws Exception {
        List<Class<? extends MapperConfig>> list = new ArrayList<>();
        Class<?> main = deduceMainApplicationClass();
        List<String> strings = ClassUtil.scanClasses(this, Objects.requireNonNull(main).getPackage().getName());
        for (String string : strings) {
            Class<?> aClass = Class.forName(string);
            for (Class<?> aClass1 : ClassUtils.getAllInterfacesForClass(aClass)) {
                if (MapperConfig.class.equals(aClass1) && !Modifier.isAbstract(aClass.getModifiers())) {
                    list.add((Class<? extends MapperConfig>) aClass);
                }
            }
        }
        init(args, list);
    }


    private void init(String[] args, List<Class<? extends MapperConfig>> mapperConfigs) throws Exception {
        if (mapperConfigs == null || mapperConfigs.isEmpty()) {
            throw new JobDefinitionException("mapperConfigs cannot be empty,please check");
        }
        List<MapperConfig> list = new ArrayList<>();
        for (Class<? extends MapperConfig> mapperConfig : mapperConfigs) {
            list.add(mapperConfig.getDeclaredConstructor().newInstance());
        }
        init(args, list.toArray(new MapperConfig[0]));
    }

    private void init(String[] args, Class<? extends MapperConfig>[] mapperConfigs) throws Exception {
        if (mapperConfigs == null || mapperConfigs.length == 0) {
            throw new JobDefinitionException("mapperConfig cannot be empty,please check");
        }
        List<MapperConfig> list = new ArrayList<>();
        for (Class<? extends MapperConfig> mapperConfig : mapperConfigs) {
            list.add(mapperConfig.getDeclaredConstructor().newInstance());
        }
        init(args, list.toArray(new MapperConfig[0]));
    }

    /**
     * 项目启动执行
     *
     * @param args          传入参数
     * @param mapperConfigs mapper定义
     */
    private void init(String[] args, MapperConfig... mapperConfigs) throws JobDefinitionException {
        if (mapperConfigs == null || mapperConfigs.length == 0) {
            throw new JobDefinitionException("mapperConfig cannot be empty,please check");
        }
        this.mapperConfigs = Arrays.stream(mapperConfigs).collect(Collectors.toList());
    }

    private Class<?> deduceMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = (new RuntimeException()).getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getMethodName());
                }
            }
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
