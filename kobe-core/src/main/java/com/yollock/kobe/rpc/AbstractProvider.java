package com.yollock.kobe.rpc;

import com.yollock.kobe.common.AbstractNode;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractProvider<T> extends AbstractNode implements Provider<T> {
    protected Class<T> clazz;
    protected Map<String, Method> methodMap = new HashMap<String, Method>();

    public AbstractProvider(URL url, Class<T> clazz) {
        super(url);
        this.clazz = clazz;
        initMethodMap(clazz);
    }

    public void destroy() {
    }

    @Override
    public boolean isAvailable() {
        return super.isAvailable();
    }

    @Override
    public String desc() {
        if (url != null) {
            return url.toString();
        }
        return null;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public Class<T> getInterface() {
        return clazz;
    }

    protected Method lookup(Request request) {
        String methodDesc = ReflectUtil.getMethodDesc(request.getMethodName(), request.getParamtersDesc());
        return methodMap.get(methodDesc);
    }

    private void initMethodMap(Class<T> clazz) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodDesc = ReflectUtil.getMethodDesc(method);
            methodMap.put(methodDesc, method);
        }
    }

}
