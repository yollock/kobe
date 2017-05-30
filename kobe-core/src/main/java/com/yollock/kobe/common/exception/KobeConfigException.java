package com.yollock.kobe.common.exception;

/**
 * Created by yollock on 2016/12/27.
 */
public class KobeConfigException extends RuntimeException{
    public KobeConfigException() {
        super();
    }

    public KobeConfigException(String message) {
        super(message);
    }

    public KobeConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeConfigException(Throwable cause) {
        super(cause);
    }

    protected KobeConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
