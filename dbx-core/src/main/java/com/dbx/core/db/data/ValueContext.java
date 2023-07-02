package com.dbx.core.db.data;

import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.db.datasource.DataSourceWrapper;

import java.util.Map;

/**
 * @author Aqoo
 */
public interface ValueContext {
    /**
     * 本条数据是否需要被处理 <br>
     * <p>典型的示列：当一条数据需要被处理成其他数据时，当某种条件下数据需要被处理成A数据，某种情况下需要被处理成B数据 </p>
     * <p>
     * <span>举例：user(id,name,age,sex) [1,'张三,10,'男'],[1,'张三,10,'男'],[1,'张三,10,'男']</span>
     * <span>==> user(id,name,sex)  user_nan(id,name,age)  user_nv(id,name,age) </span>
     * </p>
     *
     * @return
     */
    RowValueState getValueState();

    /**
     * 获取值定义id
     *
     * @return 值定义id
     * @see TableRowValueMapperDefinition
     */
    TableRowValueMapperDefinition getTableRowValueMapperDefinition();

    /**
     * 获取源表单行（全部）数据，与定义无关
     *
     * @return 原始数据对象
     */
    Map<String, Object> getSourceValue();

    /**
     * 注意；运行时中，解析一个赋值一个
     * 动态赋值
     *
     * @return 当前执行的数据对象
     */
    Map<String, Object> getTargetValue();

    /**
     * 获取源 数据源 ， 可能为null
     *
     * @return 数据源
     */
    DataSourceWrapper getSourceWrapper();

    /**
     * 获取目标数据源
     * 可能不存在
     *
     * @return 数据源
     */
    DataSourceWrapper getTargetWrapper();

    /**
     * 父级 值上下文
     *
     * @return 父级 值上下文
     */
    ValueContext getParent();

    /**
     * 获取其他数据信息
     * 此信息可以在单值循环周期内进行值的设置
     *
     * @return 多对象值
     */
    Map<String, Map<String, Object>> getOtherValues();

    /**
     * 存放数据信息
     *
     * @param key  唯一标识
     * @param data 值对象
     */
    void putOtherValues(String key, Map<String, Object> data);
}
