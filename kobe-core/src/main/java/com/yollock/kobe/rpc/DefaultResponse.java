package com.yollock.kobe.rpc;

import com.yollock.kobe.common.exception.KobeServiceException;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class DefaultResponse implements Response, Serializable {

    private Object value;
    private Exception exception;
    private long id;
    private long processTime;
    private int timeout;

    private Map<String, String> attachments;// rpc协议版本兼容时可以回传一些额外的信息

    public DefaultResponse() {
    }

    public DefaultResponse(long id) {
        this.id = id;
    }

    public DefaultResponse(Response response) {
        this.value = response.getValue();
        this.exception = response.getException();
        this.id = response.getId();
        this.processTime = response.getProcessTime();
        this.timeout = response.getTimeout();
    }

    public DefaultResponse(Object value) {
        this.value = value;
    }

    public DefaultResponse(Object value, long requestId) {
        this.value = value;
    }

    public Object getValue() {
        if (exception != null) {
            throw (exception instanceof RuntimeException) ? (RuntimeException) exception : new KobeServiceException(exception.getMessage(), exception);
        }

        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getProcessTime() {
        return processTime;
    }

    @Override
    public void setProcessTime(long time) {
        this.processTime = time;
    }

    public int getTimeout() {
        return this.timeout;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> getAttachments() {
        return attachments != null ? attachments : Collections.EMPTY_MAP;
    }

    @Override
    public void setAttachment(String key, String value) {
        if (this.attachments == null) {
            this.attachments = new HashMap<String, String>();
        }

        this.attachments.put(key, value);
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

}
