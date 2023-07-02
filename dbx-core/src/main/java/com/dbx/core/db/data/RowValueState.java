package com.dbx.core.db.data;

import com.dbx.core.constans.ValueExecState;
import lombok.Data;

/**
 * @author Aqoo
 */

@Data
public class RowValueState {
    private ValueExecState valueExecState = ValueExecState.EXEC_SQL;
}
