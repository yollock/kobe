package com.yollock.kobe.transport;


import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;
import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

public abstract class AbstractPoolClient extends AbstractClient {

    protected static long defaultMinEvictableIdleTimeMillis = (long) 1000 * 60 * 60;//默认链接空闲时间
    protected static long defaultSoftMinEvictableIdleTimeMillis = (long) 1000 * 60 * 10;//
    protected static long defaultTimeBetweenEvictionRunsMillis = (long) 1000 * 60 * 10;//默认回收周期
    protected GenericObjectPool pool;
    protected GenericObjectPool.Config poolConfig;
    protected PoolableObjectFactory factory;

    public AbstractPoolClient(URL url) {
        super(url);
    }

    protected void createPool() {
        poolConfig = new GenericObjectPool.Config();
        poolConfig.minIdle = url.getIntParameter(URLParam.minClientConnection.getName(), URLParam.minClientConnection.getIntValue());
        poolConfig.maxIdle = url.getIntParameter(URLParam.maxClientConnection.getName(), URLParam.maxClientConnection.getIntValue());
        poolConfig.maxActive = poolConfig.maxIdle;
        poolConfig.maxWait = url.getIntParameter(URLParam.requestTimeout.getName(), URLParam.requestTimeout.getIntValue());
        poolConfig.lifo = url.getBooleanParameter(URLParam.poolLifo.getName(), URLParam.poolLifo.getBooleanValue());
        poolConfig.minEvictableIdleTimeMillis = defaultMinEvictableIdleTimeMillis;
        poolConfig.softMinEvictableIdleTimeMillis = defaultSoftMinEvictableIdleTimeMillis;
        poolConfig.timeBetweenEvictionRunsMillis = defaultTimeBetweenEvictionRunsMillis;
        factory = createChannelFactory();

        pool = new GenericObjectPool(factory, poolConfig);

        boolean lazyInit = url.getBooleanParameter(URLParam.lazyInit.getName(), URLParam.lazyInit.getBooleanValue());

        if (!lazyInit) {
            for (int i = 0; i < poolConfig.minIdle; i++) {
                try {
                    pool.addObject();
                } catch (Exception e) {
                    e.printStackTrace();
                    LoggerUtil.error("NettyClient init pool create connect error: url is " + url.getUri(), e);
                }
            }
        }
    }

    protected abstract BasePoolableObjectFactory createChannelFactory();

    protected Channel borrowObject() throws Exception {
        Channel nettyChannel = (Channel) pool.borrowObject();

        if (nettyChannel != null && nettyChannel.isAvailable()) {
            return nettyChannel;
        }

        invalidateObject(nettyChannel);

        String errorMsg = ReflectUtil.simpleClassName(this) + " borrowObject error: url=" + url.getUri();
        LoggerUtil.error(errorMsg);
        throw new KobeTransportException(errorMsg);
    }

    protected void invalidateObject(Channel nettyChannel) {
        if (nettyChannel == null) {
            return;
        }
        try {
            pool.invalidateObject(nettyChannel);
        } catch (Exception ie) {
            LoggerUtil.error(ReflectUtil.simpleClassName(this) + " invalidate client error: url=" + url.getUri(), ie);
        }
    }

    protected void returnObject(Channel channel) {
        if (channel == null) {
            return;
        }

        try {
            pool.returnObject(channel);
        } catch (Exception ie) {
            LoggerUtil.error(ReflectUtil.simpleClassName(this) + " return client error: url=" + url.getUri(), ie);
        }
    }
}
