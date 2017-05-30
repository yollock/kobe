package com.yollock.kobe.rpc;

import com.yollock.kobe.common.URL;

public interface Consumer<T> extends Invoker<T> {

    // 当前使用该consumer的调用数
    int count();

    // 获取consumer的原始service url
    URL getServiceUrl();

}
