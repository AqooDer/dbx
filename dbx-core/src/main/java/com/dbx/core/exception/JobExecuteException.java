package com.dbx.core.exception;

/**
 * 任务执行异常
 * @author Aqoo
 */
public class JobExecuteException extends RuntimeException {
    public JobExecuteException() {
        super();
    }

    public JobExecuteException(String message) {
        super(message);
    }

    public JobExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecuteException(Throwable cause) {
        super(cause);
    }

    protected JobExecuteException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
