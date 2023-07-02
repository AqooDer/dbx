package com.dbx.core.db.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateTableSqLInfo {

    private String cr;

    private String dropSql;

    private String createSql;

    public String allSql() {
        return dropSql + cr + createSql;
    }
}
