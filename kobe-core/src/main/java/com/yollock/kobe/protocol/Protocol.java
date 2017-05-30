package com.yollock.kobe.protocol;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.rpc.Consumer;
import com.yollock.kobe.rpc.Provider;

public interface Protocol {

    <T> Provider<T> provide(T ref, URL url) throws InterruptedException;

    <T> Consumer<T> consume(Class<T> clazz, URL url, URL serviceUrl) throws InterruptedException;

    void destroy();
}
