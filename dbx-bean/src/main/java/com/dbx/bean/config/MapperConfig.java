package com.dbx.bean.config;

/**
 * 数据table继承
 *
 * @author Aqoo
 */
public interface MapperConfig {
    /**
     * 返回需要处理的mapper集合
     *
     * @return 返回需要处理的mapper集合
     */
    Class<?>[] getMapperConfigs();
}
