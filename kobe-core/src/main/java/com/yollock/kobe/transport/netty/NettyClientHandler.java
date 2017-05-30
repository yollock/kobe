package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private NettyClient client;

    public NettyClientHandler(NettyClient client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Response)) {
            final String message = "NettyClientHandler channelRead type not support: class is " + msg.getClass();
            LoggerUtil.error(message);
            throw new KobeTransportException(message);
        }
        Response response = (Response) msg;
        client.putResponse(response);
    }
}
