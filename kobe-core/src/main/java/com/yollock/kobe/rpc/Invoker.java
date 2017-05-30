package com.yollock.kobe.rpc;

import com.yollock.kobe.common.Node;

public interface Invoker<T> extends Node {

    Class<T> getInterface();

    Response invoke(Request request);

}
