package com.dbx.core.job;

import com.dbx.core.exception.JobException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractJob implements Job {
    protected final String id;

    protected final JobConfig jobConfig;

    protected JobDefinition jobDefinition;

    protected volatile JobState jobState;

    @SuppressWarnings("rawtypes")
    protected List<Transformer> transformers = new ArrayList<>();

    protected AbstractJob(@NonNull String id,
                          @NonNull JobConfig jobConfig) {
        this.id = id;
        this.jobConfig = jobConfig;
    }

    public void initialized() {
        this.jobState = JobState.INITIALIZED;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Transformer> getTransformers() {
        return transformers;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void run() {
        synchronized (this) {
            if (JobState.INITIALIZED == this.jobState) {
                this.jobState = JobState.RUNNING;
            } else if (JobState.RUNNING == this.jobState) {
                log.warn("The job is currently executing, please do not repeat it");
                return;
            } else if (JobState.STOP == this.jobState) {
                log.warn("The job has already been executed, please do not repeat it");
                return;
            } else if (JobState.ERROR == this.jobState) {
                log.warn("The job has a Error ,  please check.");
                return;
            } else {
                throw new JobException("Job is not initialized ,  please check.");
            }
        }
        try {
            // 链式调用
            for (Transformer transformer : getTransformers()) {
                if (jobState == JobState.ERROR || jobState == JobState.STOP) {
                    return;
                }
                transformer.transfer();
            }
            this.jobState = JobState.STOP;
        } catch (Exception e) {
            e.printStackTrace();
            this.jobState = JobState.ERROR;
        }
    }


}
