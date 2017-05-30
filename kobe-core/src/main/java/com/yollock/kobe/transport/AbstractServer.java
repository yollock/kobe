package com.yollock.kobe.transport;

import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Provider;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;


public abstract class AbstractServer implements Server {

    protected Map<String, Provider<?>> provivers;
    protected InetSocketAddress localAddress;
    protected InetSocketAddress remoteAddress;

    protected URL url;

    protected volatile ChannelState state = ChannelState.UNINIT;

    public AbstractServer(URL url, Map<String, Provider<?>> provivers) {
        this.url = url;
        this.provivers = provivers;
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
    public Collection<Channel> getChannels() {
        throw new KobeTransportException(this.getClass().getName() + " getChannels() method unsupport " + url);
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        throw new KobeTransportException(this.getClass().getName() + " getChannel(InetSocketAddress) method unsupport " + url);
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Provider<?> provider(String key) {
        return provivers.get(key);
    }

    public Map<String, Provider<?>> getProvivers() {
        return provivers;
    }
}
