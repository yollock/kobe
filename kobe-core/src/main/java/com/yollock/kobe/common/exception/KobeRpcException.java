package com.yollock.kobe.common.exception;


public class KobeRpcException extends RuntimeException {
    public KobeRpcException() {
    }

    public KobeRpcException(String message) {
        super(message);
    }

    public KobeRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeRpcException(Throwable cause) {
        super(cause);
    }

    public KobeRpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
