package com.yollock.kobe.transport.netty;

import com.google.common.util.concurrent.MoreExecutors;
import com.yollock.kobe.common.ChannelState;
import com.yollock.kobe.common.Constants;
import com.yollock.kobe.common.DefaultThreadExecutor;
import com.yollock.kobe.common.NamedThreadFactory;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.rpc.Provider;
import com.yollock.kobe.transport.AbstractServer;
import com.yollock.kobe.util.ThreadUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;


public class NettyServer extends AbstractServer {

    private ServerBootstrap bootstrap;

    // 单端口需要对应单executor 1) 为了更好的隔离性 2) 为了防止被动releaseExternalResources:
    private ThreadPoolExecutor businessExecutor = null;

    public NettyServer(URL url, Map<String, Provider<?>> provivers) {
        super(url, provivers);
    }

    @Override
    public synchronized boolean open() throws InterruptedException {
        createServerBootstrap();
        bootstrap.bind(new InetSocketAddress(url.getPort())).sync();
        state = ChannelState.ALIVE;
        return state.isAliveState();
    }

    private void createServerBootstrap() {
        boolean shareChannel = url.getBooleanParameter(URLParam.shareChannel.getName(), URLParam.shareChannel.getBooleanValue());
        int maxServerConnection = url.getIntParameter(URLParam.maxServerConnection.getName(), URLParam.maxServerConnection.getIntValue());
        int workerQueueSize = url.getIntParameter(URLParam.workerQueueSize.getName(), URLParam.workerQueueSize.getIntValue());

        int minWorkerThread = 0, maxWorkerThread = 0;
        if (shareChannel) {
            minWorkerThread = url.getIntParameter(URLParam.minWorkerThread.getName(), Constants.NETTY_SHARECHANNEL_MIN_WORKDER);
            maxWorkerThread = url.getIntParameter(URLParam.maxWorkerThread.getName(), Constants.NETTY_SHARECHANNEL_MAX_WORKDER);
        } else {
            minWorkerThread = url.getIntParameter(URLParam.minWorkerThread.getName(), Constants.NETTY_NOT_SHARECHANNEL_MIN_WORKDER);
            maxWorkerThread = url.getIntParameter(URLParam.maxWorkerThread.getName(), Constants.NETTY_NOT_SHARECHANNEL_MAX_WORKDER);
        }

        businessExecutor = (businessExecutor != null && !businessExecutor.isShutdown()) //
                ? businessExecutor //
                : new DefaultThreadExecutor(minWorkerThread, //
                maxWorkerThread, //
                workerQueueSize, //
                new NamedThreadFactory("Kobe-NettyServer-" + url.getServerPortStr(), true));
        businessExecutor.prestartAllCoreThreads();

        // 连接数的管理，进行最大连接数的限制 
        // channelManage = new NettyServerChannelManage(maxServerConnection);

        ThreadFactory bossThreadFactory = new NamedThreadFactory("Kobe-NettyServer-Boss-");
        ThreadFactory workerThreadFactory = new NamedThreadFactory("Kobe-NettyServer-Worker-");
        EventLoopGroup bossGroup = new NioEventLoopGroup(Constants.PROCESSORS, bossThreadFactory);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(Constants.PROCESSORS * 2, workerThreadFactory);
        // workerGroup.setIoRatio(Integer.parseInt(System.getProperty("nfs.rpc.io.ratio", "50")));
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup) //
                .channel(NioServerSocketChannel.class) //
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //
                .option(ChannelOption.SO_REUSEADDR, Boolean.parseBoolean(System.getProperty("tcp.reuseaddress", "true"))) //
                .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("tcp.nodelay", "true"))) //
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast("decoder", new NettyDecoder(url));
                        pipeline.addLast("encoder", new NettyEncoder(url));
                        pipeline.addLast("handler", new NettyServerHandler(NettyServer.this));
                    }
                });
    }

    @Override
    public void close() {
        close(0);
    }

    @Override
    public void close(int timeout) {
        ThreadUtil.gracefulShutdown(businessExecutor, timeout);
    }

    @Override
    public boolean isClosed() {
        return state.isCloseState();
    }

    @Override
    public boolean isAvailable() {
        return state.isAliveState();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public ThreadPoolExecutor excutor() {
        return businessExecutor;
    }

}
