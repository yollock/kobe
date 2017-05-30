package com.yollock.kobe.rpc;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.exception.KobeBizException;
import com.yollock.kobe.common.exception.KobeServiceException;
import com.yollock.kobe.common.extension.ExtensionLoader;
import com.yollock.kobe.transport.EndpointFactory;
import com.yollock.kobe.transport.NettyEndpointFactory;
import com.yollock.kobe.transport.Server;
import com.yollock.kobe.util.LoggerUtil;

import java.lang.reflect.Method;
import java.util.Map;

public class DefaultProvider<T> extends AbstractProvider<T> {

    protected T ref;
    protected Server server;
    protected EndpointFactory factory;

    public DefaultProvider(T ref, URL url, Map<String, Provider<?>> provivers) {
        super(url, (Class<T>) ref.getClass());
        this.ref = ref;
        factory = ExtensionLoader.getExtensionLoader(EndpointFactory.class).getExtension("netty");
        server = factory.createServer(url, provivers);
    }

    @Override
    protected boolean doInit() throws InterruptedException {
        return server.open();
    }

    @Override
    public Response invoke(Request request) {
        DefaultResponse response = new DefaultResponse();
        Method method = lookup(request);
        if (method == null) {
            KobeServiceException exception = new KobeServiceException("Service method not exist: " + request.getInterfaceName() + "." + request.getMethodName() + "(" + request.getParamtersDesc() + ")");
            response.setException(exception);
            return response;
        }

        try {
            Object value = method.invoke(ref, request.getArguments());
            response.setValue(value);
        } catch (Exception e) {
            if (e.getCause() != null) {
                LoggerUtil.error("Exception caught when method invoke: " + e.getCause());
                response.setException(new KobeBizException("provider call process error", e.getCause()));
            } else {
                response.setException(new KobeBizException("provider call process error", e));
            }
        } catch (Throwable t) {
            if (t.getCause() != null) {
                response.setException(new KobeServiceException("provider has encountered a fatal error!", t.getCause()));
            } else {
                response.setException(new KobeServiceException("provider has encountered a fatal error!", t));
            }
        }

        response.setAttachments(request.getAttachments());
        return response;
    }

    @Override
    public void close() {
        server.close();
    }

}
