package com.dbx.core.db.sql.resolver;

import com.dbx.core.config.TableFieldValueMapperDefinition;
import com.dbx.core.config.TableRowValueMapperDefinition;
import com.dbx.core.db.DbTransferType;

import java.util.function.Supplier;

/**
 * 处理数据值，将其转化城sql语句中的值。
 *
 * @author Aqoo
 */
public interface FieldValueResolver {

    /**
     * 用于解析数据的类型，并将其转换成sql语句中的值
     *
     * @param tfm
     * @param object
     * @return
     */
    String writeResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition tfm, Object object);

    Object execResolve(DbTransferType dbTransferType, TableRowValueMapperDefinition tvm, TableFieldValueMapperDefinition tfm, Object object);

    /**
     * @param object       原始值
     * @param nullSupplier
     * @param supplier
     * @return
     */
    default String get(Object object, Supplier<String> nullSupplier, Supplier<String> supplier) {
        if (object == null) {
            // 从 definition拿出默认值
            return nullSupplier.get();
        }
        return supplier.get();
    }

}
