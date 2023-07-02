package com.dbx.bean.config;

import com.dbx.core.db.datasource.DataSourceConfig;
import lombok.*;

/**
 * @author Aqoo
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
public class MapperDataSourceConfig implements DataSourceConfig {

    private String name;

    @NonNull
    private String url;

    private String driver;

    private String username;

    private String password;

    private String ip;
}
