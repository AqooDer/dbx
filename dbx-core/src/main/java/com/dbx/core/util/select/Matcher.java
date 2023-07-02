package com.dbx.core.util.select;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Aqoo
 */
public interface Matcher<T, R> {
    /**
     * 条件匹配 相当于if
     *
     * @return 判断条件
     */
    Predicate<T> match();

    /**
     * 匹配执行 相当于if中的执行体
     *
     * @return 值工厂
     */
    Function<T, R> executor();

    /**
     * 工厂方法，快速创建分支
     *
     * @param match    测试器
     * @param executor 执行体
     * @param <T>      参数类型
     * @param <R>      值类型
     * @return 返回一个新的分支
     */
    static <T, R> Matcher<T, R> of(Predicate<T> match, Function<T, R> executor) {
        return new Matcher<T, R>() {
            @Override
            public Predicate<T> match() {
                return match;
            }

            @Override
            public Function<T, R> executor() {
                return executor;
            }
        };
    }
}
