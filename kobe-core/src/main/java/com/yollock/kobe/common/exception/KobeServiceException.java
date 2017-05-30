package com.yollock.kobe.common.exception;

/**
 * Created by yollock on 2016/12/27.
 */
public class KobeServiceException extends RuntimeException {
    public KobeServiceException() {
        super();
    }

    public KobeServiceException(String message) {
        super(message);
    }

    public KobeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeServiceException(Throwable cause) {
        super(cause);
    }

    protected KobeServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
