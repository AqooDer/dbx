package com.dbx.core.job;

/**
 * 从某一个源读取数据
 *
 * @return
 */
@Deprecated
public interface Reader<R> {
    R read();

}
