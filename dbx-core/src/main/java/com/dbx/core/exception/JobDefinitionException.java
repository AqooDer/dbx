package com.dbx.core.exception;

/**
 * 关于job定义的相关异常
 * @author Aqoo
 */
public class JobDefinitionException extends JobException {
    public JobDefinitionException() {
        super();
    }

    public JobDefinitionException(String message) {
        super(message);
    }

    public JobDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobDefinitionException(Throwable cause) {
        super(cause);
    }

    protected JobDefinitionException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
