package com.yollock.kobe.rpc;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.exception.KobeRpcException;
import com.yollock.kobe.common.extension.ExtensionLoader;
import com.yollock.kobe.transport.Client;
import com.yollock.kobe.transport.EndpointFactory;
import com.yollock.kobe.transport.NettyEndpointFactory;

public class DefaultConsumer<T> extends AbstractConsumer<T> {

    protected Client client;
    protected EndpointFactory factory;

    public DefaultConsumer(Class<T> clazz, URL url, URL serviceUrl) {
        super(clazz, url, serviceUrl);
        factory = ExtensionLoader.getExtensionLoader(EndpointFactory.class).getExtension("netty");
        client = factory.createClient(url);
    }

    @Override
    protected Response doInvoke(Request request) {
        try {
            // 为了能够实现跨group请求，需要使用server端的group。
//        request.setAttachment(URLParam.group.getName(), serviceUrl.getGroup());

            return client.request(request);
        } catch (Exception e) {
            throw new KobeRpcException(""); // TODO: 2017/1/18  
        }
    }

    @Override
    protected boolean doInit() throws InterruptedException {
        return client.open();
    }

    @Override
    public void close() {

    }
}
