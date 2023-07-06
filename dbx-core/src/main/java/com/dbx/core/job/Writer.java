package com.dbx.core.job;

/**
 * 输出数据
 *
 * @deprecated 用不到
 */
@Deprecated
public interface Writer<W> {
    void write(W object);
}
