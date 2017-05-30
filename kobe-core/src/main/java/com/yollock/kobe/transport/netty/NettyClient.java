package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.Constants;
import com.yollock.kobe.common.NamedThreadFactory;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.transport.AbstractPoolClient;
import com.yollock.kobe.transport.Channel;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.concurrent.ConcurrentHashMapV8;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.pool.BasePoolableObjectFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class NettyClient extends AbstractPoolClient {

    protected static ConcurrentHashMapV8<Long, ArrayBlockingQueue<Object>> responses = new ConcurrentHashMapV8<Long, ArrayBlockingQueue<Object>>();

    private Bootstrap bootstrap;

    public NettyClient(URL url) {
        super(url);
    }

    @Override
    public Response request(Request request) throws KobeTransportException {
        if (!isAvailable()) {
            throw new KobeTransportException(""); // TODO: 2017/1/9
        }
        final int timeout = url.getIntParameter(URLParam.connectTimeout.getName(), URLParam.connectTimeout.getIntValue());
        long beginTime = System.currentTimeMillis();
        ArrayBlockingQueue<Object> responseQueue = new ArrayBlockingQueue<Object>(1);
        responses.put(request.getId(), responseQueue);

        Object result = null;
        Channel channel = null;
        try {
            channel = borrowObject();
            if (channel == null) {
                // TODO: 2017/1/9
            }
            channel.request(request, timeout);

            try {
                result = responseQueue.poll(timeout - (System.currentTimeMillis() - beginTime), TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LoggerUtil.error("Get response error", e);
                throw new KobeTransportException("Get response error", e);
            } finally {
                responses.remove(request.getId());
            }

            if (result == null) {
                // TODO: 2017/1/19
            }

            returnObject(channel);
        } catch (Exception e) {
            invalidateObject(channel);
        }
        return (Response) result;
    }

    @Override
    public void heartbeat(Request request) {

    }

    @Override
    public synchronized boolean open() throws InterruptedException {
        if (isAvailable()) {
            return true;
        }

        // 初始化netty client bootstrap
        createBootstrp();

        // 初始化连接池
        createPool();

        LoggerUtil.info("NettyClient finish Open: url={}", url);

        // 注册统计回调
        //        StatsUtil.registryStatisticCallback(this);

        // 设置可用状态
        state = ChannelState.ALIVE;
        return state.isAliveState();
    }

    private void createBootstrp() {
        ThreadFactory workerThreadFactory = new NamedThreadFactory("Kobe-NettyServer-Worker-");
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.PROCESSORS * 2, workerThreadFactory);
        bootstrap = new Bootstrap();
        bootstrap.group(workerGroup) //
                .channel(NioSocketChannel.class) //
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
                .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("tcp.nodelay", "true"))) //
                .option(ChannelOption.SO_REUSEADDR, Boolean.parseBoolean(System.getProperty("tcp.reuseaddress", "true")));

        //        int timeout = getUrl().getIntParameter(URLParam.requestTimeout.getName(), URLParam.requestTimeout.getIntValue());
        int timeout = 2000;
        if (timeout < 1000) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
        } else {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout);
        }

        final NettyClientHandler handler = new NettyClientHandler(this);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel channel) throws Exception {
                ChannelPipeline pipeline = channel.pipeline();
                pipeline.addLast("decoder", new NettyDecoder(url));
                pipeline.addLast("encoder", new NettyEncoder(url));
                pipeline.addLast("handler", handler);
            }
        });
    }

    @Override
    protected BasePoolableObjectFactory createChannelFactory() {
        return new NettyChannelFactory(this);
    }


    @Override
    public void close() {

    }

    @Override
    public void close(int timeout) {

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
        return url;
    }


    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    public void putResponse(Response response) {
        try {
            ArrayBlockingQueue<Object> queue = responses.get(response.getId());
            if (queue == null) {
                LoggerUtil.warn("give up the response,request id is:" + response.getId() + ",maybe because timeout!");
                return;
            }
            queue.put(response);
        } catch (InterruptedException e) {
            LoggerUtil.warn("put response error,request id is:" + response.getId(), e);
        }
    }
}
