package com.yollock.kobe.transport;


import com.yollock.kobe.rpc.Request;

public interface Client extends Channel {

    void heartbeat(Request request);

}
