package com.yollock.kobe.common.exception;

import java.io.IOException;


public class KobeSerializeException extends IOException {

    public KobeSerializeException() {
        super();
    }

    public KobeSerializeException(String message) {
        super(message);
    }

    public KobeSerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KobeSerializeException(Throwable cause) {
        super(cause);
    }

}
