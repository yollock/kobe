package com.yollock.kobe.rpc;

import java.io.Serializable;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

public class DefaultRequest implements Request, Serializable {

    private String interfaceName;
    private String methodName;
    private String paramtersDesc;
    private Object[] arguments;
    private Map<String, String> attachments;
    private int retries = 0;

    private long id;

    private byte rpcProtocolVersion = (byte) 1; // TODO: 2016/12/29

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParamtersDesc() {
        return paramtersDesc;
    }

    public void setParamtersDesc(String paramtersDesc) {
        this.paramtersDesc = paramtersDesc;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

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

    @Override
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public String toString() {
        return interfaceName + "." + methodName + "(" + paramtersDesc + ") requestId=" + id;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }


}
