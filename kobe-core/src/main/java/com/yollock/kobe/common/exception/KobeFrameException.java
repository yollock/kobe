package com.yollock.kobe.common.exception;

/**
 * Created by yollock on 2016/12/27.
 */
public class KobeFrameException extends RuntimeException {

    public KobeFrameException() {
        super();
    }

    public KobeFrameException(String message) {
        super(message);
    }

    public KobeFrameException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeFrameException(Throwable cause) {
        super(cause);
    }

    protected KobeFrameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
