package com.dbx.core.exception;

/**
 * @author Aqoo
 */
public class JobDataSourceException extends RuntimeException {
    public JobDataSourceException() {
        super();
    }

    public JobDataSourceException(String message) {
        super(message);
    }

    public JobDataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobDataSourceException(Throwable cause) {
        super(cause);
    }

    protected JobDataSourceException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
