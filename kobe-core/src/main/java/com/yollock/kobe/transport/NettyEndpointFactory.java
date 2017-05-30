package com.yollock.kobe.transport;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.rpc.Provider;
import com.yollock.kobe.transport.netty.NettyClient;
import com.yollock.kobe.transport.netty.NettyServer;

import java.util.Map;

public class NettyEndpointFactory extends AbstractEndpointFactory {

    public NettyEndpointFactory() {
        super();
    }

    @Override
    protected Server subCreateServer(URL url, Map<String, Provider<?>> provivers) {
        return new NettyServer(url, provivers);
    }

    @Override
    protected Client subCreateClient(URL url) {
        return new NettyClient(url);
    }

}
