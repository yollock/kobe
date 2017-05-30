package com.yollock.kobe.common;


public class Message {
    private int status;
    private int errorcode;
    private String message;

    public Message(int status, int errorcode, String message) {
        this.status = status;
        this.errorcode = errorcode;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorcode;
    }

    public String getMessage() {
        return message;
    }

}
