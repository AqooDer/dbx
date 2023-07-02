package com.dbx.core.config;

import com.dbx.core.db.DataSourceMapping;
import com.dbx.core.db.datasource.model.TableModel;
import lombok.NonNull;

import java.util.Map;

/**
 * 数据表定义的所有信息：
 * 1.数据源上下文
 * 2.数据表名称
 * 3.数据表ddl信息
 * 4.数据表字段映射以及转换信息。
 * <p>
 * 该数据结构支持：
 * 1.一个表的字段转换到另一个表行数据
 * 2.数据来源于不同的数据库的不同表
 *
 * @author Aqoo
 */
public interface TableMapperDefinition {
    /**
     * MapperDefinition 的id ，区别多链接，多库时的唯一定义
     * 一般说来都是：db的 scheme+targetTableName
     *
     * @return 返回坐标id
     */
    @NonNull
    String getId();

    /**
     * 获取数据源配置
     *
     * @return 数据源上下文
     */
    @NonNull
    DataSourceMapping getDataSourceMapping();

    /**
     * 获取配置信息
     *
     * @return 配置信息
     */

    //@NonNull MapperProperties getMapperProperties();

    /**
     * 获取表模型信息
     * 注意：该表模型是根据DDL模型生成。
     *
     * @return 表模型
     */
    TableModel getTableModel();

    /**
     * 在子表中或者完全不依赖源表的情况下可能为空
     *
     * @return
     */
    TableModel getSourceTableModel();

    /**
     * 获取字段数据转换定义
     *
     * @return 获取字段数据转换定义
     */
    @NonNull
    Map<String, TableRowValueMapperDefinition> getFieldMapperDefinitions();


}
