package com.yollock.kobe.transport;

import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.util.LoggerUtil;

import java.net.InetSocketAddress;

public abstract class AbstractClient implements Client {

    protected InetSocketAddress localAddress;
    protected InetSocketAddress remoteAddress;

    protected URL url;

    protected volatile ChannelState state = ChannelState.UNINIT;

    public AbstractClient(URL url) {
        this.url = url;
        LoggerUtil.info("init nettyclient. url:" + url.getHost() + "-" + url.getPath());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setLocalAddress(InetSocketAddress localAddress) {
        this.localAddress = localAddress;
    }

    public void setRemoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
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
