package com.yollock.kobe.transport;

import com.yollock.kobe.common.URL;

import java.net.InetSocketAddress;

/**
 * Created by yollock on 2016/12/28.
 */
public interface Endpoint {

    boolean open() throws InterruptedException;

    void close();

    void close(int timeout);

    boolean isClosed();

    boolean isAvailable();

    URL getUrl();

    InetSocketAddress getLocalAddress();

    InetSocketAddress getRemoteAddress();

}
