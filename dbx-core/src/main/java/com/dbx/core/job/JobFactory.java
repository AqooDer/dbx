package com.dbx.core.job;

import lombok.NonNull;

public interface JobFactory {
    @NonNull Job getJob();
}
