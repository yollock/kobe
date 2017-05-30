package com.yollock.kobe.filter;


import com.yollock.kobe.rpc.Invoker;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;

public interface Filter {

    Response filter(Invoker<?> caller, Request request);

}
