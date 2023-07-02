package com.dbx.core.job;

/**
 * Channel 是一个管道，是一个中间操作，不做任何终端输出
 * 将read的数据转成需要write的数据
 *
 * @return
 */
public interface Channel<R ,W> {
    W transferTo(R data);
}
