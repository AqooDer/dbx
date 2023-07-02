package com.dbx.core.db.data;

import com.dbx.core.config.TableFieldValueMapperDefinition;

/**
 * 注意事项： 当
 *
 * @author Aqoo
 */
public interface ValueFormat {
    /**
     * 数据开始处理的回掉函数 <br>
     * 本函数在在单条数据处理周期中只会被执行一次 <br>
     *
     * @param valueContext 本次数据的上下文信息
     */
    void prepare(ValueContext valueContext);

    /**
     * 使用者自定义的格式化数据的方法 <br>
     * 本函数在单条数据处理周期中会被执行多次。<br>
     * 请注意：当该类存在时，对数据的处理优先级最高，所以当不用该方法处理某个值时，请返回null。<br>
     *
     * @param fvm          本次数据的定义模型
     * @param valueContext 本次数据的上下文信息
     * @return 处理后的数据
     */
    Object format(TableFieldValueMapperDefinition fvm, ValueContext valueContext);

    /**
     * 数据处理结束回掉函数
     * 数据开始处理的回掉函数
     *
     * @param valueContext 本次数据的上下文信息
     */
    void end(ValueContext valueContext);
}
