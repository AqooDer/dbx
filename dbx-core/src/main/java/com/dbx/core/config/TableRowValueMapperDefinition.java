package com.dbx.core.config;

import com.dbx.core.constans.TableValueExecStatus;
import com.dbx.core.db.data.ValueFormat;
import com.dbx.core.db.datasource.model.TableModel;

import java.util.List;

/**
 * 一个表的定义信息和其附属信息
 *
 * @author Aqoo
 */
public interface TableRowValueMapperDefinition {
    /**
     * TableValueMapperDefinition 的id
     *
     * @return 返回标识
     */
    String getId();

    /**
     * 返回该row对应的 TableMapperDefinition
     *
     * @return 表定义
     */
    TableMapperDefinition getTableMapperDefinition();

    /**
     * 返回表对应的源表的数据模型
     * 注意：该模型是根据原表配置查询而出 。
     *
     * @return
     */
    TableModel getSourceValueTableModel();

    /**
     * 子表，字表只能挂靠于主信息上，当处理主表信息时，即时处理字表数据信息
     *
     * @return 返回子表定义信息
     */
    TableRowValueMapperDefinition[] child();

    /**
     * 数据格式化方法：当前数据转换定义格式的
     *
     * @return 数据格式化方法
     */
    List<TableFieldValueMapperDefinition> getFieldValueMapperDefinition();

    /**
     * 获取数据格式化方法代码
     *
     * @return 数据格式化
     */
    ValueFormat getValueFormat();

    /**
     * 数据库表数据执行状态
     * @return
     */
    TableValueExecStatus getTableValueExecStatus();
}
