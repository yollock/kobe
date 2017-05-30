package com.yollock.kobe.common.exception;

public class KobeBizException extends RuntimeException {

    public KobeBizException() {
    }

    public KobeBizException(String message) {
        super(message);
    }

    public KobeBizException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeBizException(Throwable cause) {
        super(cause);
    }

    public KobeBizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
