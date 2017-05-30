package com.yollock.kobe.transport;

import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;

import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractChannel implements com.yollock.kobe.transport.Channel {

    protected volatile ChannelState state = ChannelState.UNINIT;
    protected InetSocketAddress remoteAddress = null;
    protected InetSocketAddress localAddress = null;

    public AbstractChannel(InetSocketAddress inetSocketAddress) {
        this.remoteAddress = inetSocketAddress;
    }

    @Override
    public Response request(Request request) throws KobeTransportException {
        throw new KobeTransportException("");
    }

    @Override
    public Response request(Request request, int timeout) throws KobeTransportException {
        throw new KobeTransportException("");
    }
}
