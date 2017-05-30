package com.yollock.kobe.transport;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.extension.Extension;
import com.yollock.kobe.rpc.Provider;

import java.util.Map;

@Extension
public interface EndpointFactory {

    Server createServer(URL url, Map<String, Provider<?>> provivers);

    Client createClient(URL url);

    void closeServer(Server server, URL url);

    void closeClient(Client client, URL url);

}
