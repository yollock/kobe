package com.yollock.kobe.protocol;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.rpc.Consumer;
import com.yollock.kobe.rpc.DefaultConsumer;
import com.yollock.kobe.rpc.DefaultProvider;
import com.yollock.kobe.rpc.Provider;

import java.util.Map;

public class DefaultProtocol extends AbstractProtocol {

    @Override
    protected <T> Provider<T> createProvider(T ref, URL url, Map<String, Provider<?>> provivers) {
        return new DefaultProvider<T>(ref, url, provivers);
    }

    @Override
    protected <T> Consumer<T> createConsumer(Class<T> clazz, URL url, URL serviceUrl) {
        return new DefaultConsumer<T>(clazz, url, serviceUrl);
    }

}