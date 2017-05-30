package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.util.LoggerUtil;
import org.apache.commons.pool.BasePoolableObjectFactory;

public class NettyChannelFactory extends BasePoolableObjectFactory {
    private String factoryName = "";
    private NettyClient nettyClient;

    public NettyChannelFactory(NettyClient nettyClient) {
        super();
        this.nettyClient = nettyClient;
        this.factoryName = "NettyChannelFactory_" + nettyClient.getUrl().getHost() + "_" + nettyClient.getUrl().getPort();
    }

    public String getFactoryName() {
        return factoryName;
    }

    @Override
    public String toString() {
        return factoryName;
    }

    @Override
    public Object makeObject() throws Exception {
        NettyChannel nettyChannel = new NettyChannel(nettyClient);
        nettyChannel.open();
        return nettyChannel;
    }

    @Override
    public void destroyObject(final Object obj) throws Exception {
        if (!(obj instanceof NettyChannel)) {
            return;
        }

        NettyChannel client = (NettyChannel) obj;
        URL url = nettyClient.getUrl();
        try {
            client.close();
            LoggerUtil.info(factoryName + " client disconnect Success: " + url.getUri());
        } catch (Exception e) {
            LoggerUtil.error(factoryName + " client disconnect Error: " + url.getUri(), e);
        }
    }

    @Override
    public boolean validateObject(final Object obj) {
        if (!(obj instanceof NettyChannel)) {
            return false;
        }

        final NettyChannel client = (NettyChannel) obj;
        try {
            return client.isAvailable();
        } catch (final Exception e) {
            return false;
        }
    }

    @Override
    public void activateObject(Object obj) throws Exception {
        if (!(obj instanceof NettyChannel)) {
            return;
        }
        final NettyChannel client = (NettyChannel) obj;
        if (!client.isAvailable()) {
            client.open();
        }
    }

    @Override
    public void passivateObject(Object obj) throws Exception {
    }
}