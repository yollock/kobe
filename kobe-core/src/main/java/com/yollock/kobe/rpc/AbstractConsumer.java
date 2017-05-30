package com.yollock.kobe.rpc;

import com.yollock.kobe.common.AbstractNode;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.exception.KobeRpcException;
import com.yollock.kobe.util.ReflectUtil;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractConsumer<T> extends AbstractNode implements Consumer<T> {

    protected Class<T> clz;
    protected AtomicInteger activeRefererCount = new AtomicInteger(0);
    protected URL serviceUrl;

    public AbstractConsumer(Class<T> clz, URL url) {
        super(url);
        this.clz = clz;
        this.serviceUrl = url;
    }

    public AbstractConsumer(Class<T> clz, URL url, URL serviceUrl) {
        super(url);
        this.clz = clz;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response invoke(Request request) {
        if (!isAvailable()) {
            throw new KobeRpcException(ReflectUtil.simpleClassName(this) + " call Error: node is not available, url = " + url.getUri() + " " + request.toString());
        }

        incrActiveCount(request);
        Response response = null;
        try {
            return doInvoke(request);
        } finally {
            decrActiveCount(request, response);
        }
    }

    @Override
    public int count() {
        return activeRefererCount.get();
    }

    protected void incrActiveCount(Request request) {
        activeRefererCount.incrementAndGet();
    }

    protected void decrActiveCount(Request request, Response response) {
        activeRefererCount.decrementAndGet();
    }

    protected abstract Response doInvoke(Request request);

    @Override
    public String desc() {
        return "[" + ReflectUtil.simpleClassName(this) + "] url=" + url;
    }

    @Override
    public URL getServiceUrl() {
        return serviceUrl;
    }

}
