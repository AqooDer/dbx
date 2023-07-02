package com.dbx.core.util.select;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Aqoo
 */
public class Selector<P, T> {

    private boolean selected = false;

    private Function<P, T> executor;

    /**
     * 选择器参数，该参数会在进行条件判断和结果获取时会被当做条件传入
     */
    private final P param;

    public Selector(P param) {
        this.param = param;
    }

    public Selector<P, T> match(Predicate<P> match, Function<P, T> executor) {
        Matcher<P, T> branch = Matcher.of(match, executor);
        return match(branch);
    }

    /**
     * 传入一个新的分支，如果这个分支满足条件 则结束
     *
     * @param branch 则当前选择器将接受当前分支的结果并完成
     * @return 选择器自身
     */
    public Selector<P, T> match(Matcher<P, T> branch) {
        if (!selected) {
            boolean pass = branch.match().test(param);
            if (pass) {
                selected = true;
                executor = branch.executor();
            }
        }
        return this;
    }

    public T end() {
        if (selected) {
            return executor.apply(param);
        }
        return null;
    }

    /**
     * 在不想返回null值时
     *
     * @param elseFunction 默认返回值情况
     * @return 返回数据值
     */
    public T orElse(Function<P, T> elseFunction) {
        if (selected) {
            return executor.apply(param);
        } else {
            return elseFunction.apply(param);
        }
    }
}
