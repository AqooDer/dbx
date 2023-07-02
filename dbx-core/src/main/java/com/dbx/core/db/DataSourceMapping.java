package com.dbx.core.db;

import com.dbx.core.db.datasource.*;
import lombok.Getter;
import lombok.NonNull;

/**
 * 数据源上下文容器
 * source和target都可以为空
 * 但是source为空的情况可能性比较小
 *
 * @author Aqoo
 */
@Getter
public class DataSourceMapping {
    @NonNull
    private final DbTransferType dbTransferType;

    private final DataSourceWrapper sourceWrapper;

    private final DataSourceWrapper targetWrapper;


    public DataSourceMapping(DbTransferType dbTransferType, DataSourceConfig sourceConfig, DataSourceConfig targetConfig) {
        this(dbTransferType, sourceConfig == null ? null : new DefaultDataSourceImpl(sourceConfig), targetConfig == null ? null : new DefaultDataSourceImpl(targetConfig));
    }

    public DataSourceMapping(DbTransferType dbTransferType, JobDataSource sourceDataSource, JobDataSource targetSource) {
        this(dbTransferType, sourceDataSource == null ? null : new BasicDataSourceWrapper(sourceDataSource),
                targetSource == null ? null : new BasicDataSourceWrapper(targetSource));
    }

    public DataSourceMapping(@NonNull DbTransferType dbTransferType, DataSourceWrapper sourceWrapper, DataSourceWrapper targetWrapper) {
        this.dbTransferType = dbTransferType;
        this.sourceWrapper = sourceWrapper;
        this.targetWrapper = targetWrapper;
    }

    public boolean equals(DataSourceConfig sourceConfig, DataSourceConfig targetConfig) {
        return equals(sourceConfig == null ? "" : sourceConfig.getUrl(), targetConfig == null ? "" : targetConfig.getUrl());
    }

    public boolean equals(String sourceUrl, String targetUrl) {
        return equals(sourceWrapper, sourceUrl) && equals(targetWrapper, targetUrl);
    }

    private boolean equals(DataSourceWrapper wrapper, String url) {
        return (wrapper == null && url == null) || (wrapper != null && wrapper.getDataSource().getUrl().equals(url));
    }
}
