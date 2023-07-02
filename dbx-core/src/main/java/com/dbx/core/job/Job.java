package com.dbx.core.job;

import java.util.List;

/**
 * 负责容器的全生命周期的管理
 * 一个job对应一次处理，所有的处理是用同一个上下文，比如 1对1的数据库配置等等。
 *
 * @author Aqoo
 */
public interface Job {

    enum JobState {
        INITIALIZED,

        RUNNING,

        STOP,

        ERROR,
    }


    JobDefinition getJobDefinition();


    @SuppressWarnings("rawtypes")
    List<Transformer> getTransformers();

    /**
     * 执行，更新数据
     */
    void run();


}
