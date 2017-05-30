package com.yollock.kobe.common.exception;

/**
 * Created by yollock on 2016/12/28.
 */
public class KobeTransportException extends RuntimeException{
    public KobeTransportException() {
        super();
    }

    public KobeTransportException(String message) {
        super(message);
    }

    public KobeTransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeTransportException(Throwable cause) {
        super(cause);
    }

    protected KobeTransportException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
