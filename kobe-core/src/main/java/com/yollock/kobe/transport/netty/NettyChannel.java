package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.common.exception.KobeConfigException;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.transport.AbstractChannel;
import com.yollock.kobe.util.LoggerUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyChannel extends AbstractChannel {

    private NettyClient client;

    private io.netty.channel.Channel channel = null;

    public NettyChannel(NettyClient client) {
        super(new InetSocketAddress(client.getUrl().getHost(), client.getUrl().getPort()));
        this.client = client;
    }

    @Override
    public Response request(final Request request, final int timeout) throws KobeTransportException {
        final long beginTime = System.currentTimeMillis();
        final io.netty.channel.Channel channel = this.channel;
        if (timeout <= 0) {
            throw new KobeConfigException("NettyClient init Error: timeout(" + timeout + ") <= 0 is forbid.", null); // TODO: 2017/1/4
        }

        ChannelFuture future = channel.writeAndFlush(request);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    return;
                }
                String errorMsg = "";
                // write timeout
                if (System.currentTimeMillis() - beginTime >= timeout) {
                    errorMsg = "write to send buffer consume too long time(" + (System.currentTimeMillis() - beginTime) + "), request id is:" + request.getId();
                }
                if (future.isCancelled()) {
                    errorMsg = "Send request to " + channel.toString() + " cancelled by user,request id is:" + request.getId();
                }
                if (!future.isSuccess()) {
                    if (channel.isOpen()) {
                        // maybe some exception,so close the channel
                        channel.close();
                    } else {
                        // NettyClientFactory.getInstance().removeClient(key, self);
                    }
                    errorMsg = "Send request to " + channel.toString() + " error" + future.cause();
                }
                LoggerUtil.error(errorMsg);
            }
        });
        return null; // 此处返回null,调用者不应该接收,仅针对Netty异步通信模式
    }

    @Override
    public boolean open() {
        if (isAvailable()) {
            LoggerUtil.warn("the channel already open, local: " + super.localAddress + " remote: " + super.remoteAddress + " url: " + client.getUrl().getUri());
            return true;
        }

        try {
            ChannelFuture future = client.getBootstrap().connect(new InetSocketAddress(client.getUrl().getHost(), client.getUrl().getPort()));

            int timeout = client.getUrl().getIntParameter(URLParam.connectTimeout.getName(), URLParam.connectTimeout.getIntValue());
            if (timeout <= 0) {
                throw new KobeConfigException("NettyClient init Error: timeout(" + timeout + ") <= 0 is forbid.", null); // TODO: 2017/1/4
            }

            // 不依赖于connectTimeout, 依赖ChannelFuture.awaitUninterruptibly(timeout)
            future.awaitUninterruptibly(timeout, TimeUnit.MILLISECONDS);
            if (!future.isDone()) {
                throw new KobeTransportException("Create connection to " + client.getUrl().getHost() + ":" + client.getUrl().getPort() + " timeout!");
            }
            if (future.isCancelled()) {
                throw new KobeTransportException("Create connection to " + client.getUrl().getHost() + ":" + client.getUrl().getPort() + " cancelled by user!");
            }
            if (!future.isSuccess()) {
                throw new KobeTransportException("Create connection to " + client.getUrl().getHost() + ":" + client.getUrl().getPort() + " error", future.cause());
            }

            channel = future.channel();
            state = ChannelState.ALIVE;
        } catch (KobeTransportException e) {
            throw e;
        } catch (Exception e) {
            throw new KobeTransportException("NettyChannel failed to connect to server, url: " + client.getUrl().getUri(), e);
        } finally {
            if (!state.isAliveState()) {
                // client.incrErrorCount();
            }
        }
        return true;
    }

    @Override
    public synchronized void close() {
        close(0);
    }

    @Override
    public synchronized void close(int timeout) {
        try {
            state = ChannelState.CLOSE;

            if (channel != null) {
                channel.close();
            }
        } catch (Exception e) {
            LoggerUtil.error("NettyChannel close Error: " + client.getUrl().getUri() + " local=" + localAddress, e);
        }
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isAvailable() {
        return state.isAliveState();
    }

    @Override
    public URL getUrl() {
        return client.getUrl();
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return null;
    }
}
