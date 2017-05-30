package com.yollock.kobe.protocol;


import com.google.common.base.Preconditions;
import com.yollock.kobe.common.Node;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.rpc.Consumer;
import com.yollock.kobe.rpc.Provider;
import com.yollock.kobe.util.KobeUtil;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;
import com.yollock.kobe.util.concurrent.ConcurrentHashMapV8;

import java.util.Map;

public abstract class AbstractProtocol implements Protocol {

    // key = interface:port
    final ConcurrentHashMapV8<String, Provider<?>> provivers = new ConcurrentHashMapV8<>();

    @Override
    public <T> Provider<T> provide(T ref, URL url) throws InterruptedException {
        Preconditions.checkNotNull(url, ReflectUtil.simpleClassName(this) + " provide error: url is null");

        String protocolKey = KobeUtil.getProtocolKey(url);

        synchronized (provivers) {
            Provider<T> provider = (Provider<T>) provivers.get(protocolKey);

            Preconditions.checkNotNull(url, ReflectUtil.simpleClassName(this) + " provider already exist: url is " + url);

            provider = createProvider(ref, url, provivers);
            provider.init();
            provivers.put(protocolKey, provider);

            LoggerUtil.info(ReflectUtil.simpleClassName(this) + " provide success: url is " + url);
            return provider;
        }
    }

    protected abstract <T> Provider<T> createProvider(T ref, URL url, Map<String, Provider<?>> provivers);

    @Override
    public <T> Consumer<T> consume(Class<T> clazz, URL url, URL serviceUrl) throws InterruptedException {
        Preconditions.checkNotNull(url, ReflectUtil.simpleClassName(this) + " consume error: url is null");
        Preconditions.checkNotNull(clazz, ReflectUtil.simpleClassName(this) + " export error: provider is null, url is " + url);

        Consumer<T> consumer = createConsumer(clazz, url, serviceUrl);
        consumer.init();

        LoggerUtil.info(ReflectUtil.simpleClassName(this) + " consume success: url is " + url);
        return consumer;
    }

    protected abstract <T> Consumer<T> createConsumer(Class<T> clazz, URL url, URL serviceUrl);

    @Override
    public void destroy() {
        for (String key : provivers.keySet()) {
            Node node = provivers.remove(key);
            if (node == null) {
                return;
            }
            try {
                node.close();
                LoggerUtil.info(ReflectUtil.simpleClassName(this) + " destroy node success: " + node);
            } catch (Throwable e) {
                LoggerUtil.error(ReflectUtil.simpleClassName(this) + " destroy error", e);
            }
        }
    }

}




