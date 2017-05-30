package com.yollock.kobe.rpc;

import java.util.Map;

public interface Request {

    long getId();

    String getInterfaceName();

    String getMethodName();

    String getParamtersDesc();

    Object[] getArguments();

    Map<String, String> getAttachments();

    void setAttachment(String name, String value);

    int getRetries();

    void setRetries(int retries);

}
