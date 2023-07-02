package com.dbx.core.job;

/**
 * 输出数据
 *
 * @return
 */
@Deprecated
public interface Writer<W> {
    void write(W object);
}
