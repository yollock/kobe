package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.exception.KobeRpcException;
import com.yollock.kobe.common.exception.KobeSerializeException;
import com.yollock.kobe.common.exception.KobeServiceException;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.DefaultResponse;
import com.yollock.kobe.rpc.Provider;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.util.KobeUtil;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private NettyServer server;

    public NettyServerHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Request)) {
            final String message = "NettyServerHandler channelRead type not support: class is " + msg.getClass();
            LoggerUtil.error(message);
            throw new KobeTransportException(message);
        }
        Request request = (Request) msg;
        handleRequest(ctx, request);
    }

    private void handleRequest(final ChannelHandlerContext ctx, final Request request) {
        final long startTime = System.currentTimeMillis();
        try {
            server.excutor().execute(new Runnable() {
                @Override
                public void run() {
                    processRequest(ctx, request, startTime);
                }
            });
        } catch (RejectedExecutionException ree) {
            DefaultResponse response = new DefaultResponse();
            response.setId(request.getId());
            response.setException(new KobeServiceException("process thread pool is full, reject"));
            response.setProcessTime(System.currentTimeMillis() - startTime);
            ctx.channel().write(response);

            LoggerUtil.debug("process thread pool is full, reject, active={} poolSize={} corePoolSize={} maxPoolSize={} taskCount={} requestId={}", //
                    server.excutor().getActiveCount(), server.excutor().getPoolSize(),//
                    server.excutor().getCorePoolSize(), server.excutor().getMaximumPoolSize(),//
                    server.excutor().getTaskCount(), request.getId());
        }
    }

    private void processRequest(ChannelHandlerContext ctx, Request request, long startTime) {
        final io.netty.channel.Channel channel = ctx.channel();

        String serviceKey = KobeUtil.getServiceKey(request);
        Provider<?> provider = server.provider(serviceKey);

        if (provider == null) {
            LoggerUtil.error("NettyServerHandler handler Error: provider not exist serviceKey=" + serviceKey + " " + KobeUtil.toString(request));
            KobeRpcException exception = new KobeRpcException("NettyServerHandler handler Error: provider not exist serviceKey = " + serviceKey);

            DefaultResponse response = new DefaultResponse();
            response.setException(exception);
            response.setProcessTime(System.currentTimeMillis() - startTime);
            channel.writeAndFlush(response);
            return;
        }

        if (channel.isActive()) {
            channel.writeAndFlush(provider.invoke(request));
        }
    }


}
