package com.yollock.kobe.transport;

public interface EndpointManager {

    void init();

    void destroy();

    void addEndpoint(Endpoint endpoint);

    void removeEndpoint(Endpoint endpoint);

}
