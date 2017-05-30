package com.yollock.kobe.transport.netty;


import com.yollock.kobe.common.Constants;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.common.exception.KobeSerializeException;
import com.yollock.kobe.common.extension.ExtensionLoader;
import com.yollock.kobe.rpc.Request;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.rpc.Version;
import com.yollock.kobe.serialize.FastJsonSerialization;
import com.yollock.kobe.serialize.Serialization;
import com.yollock.kobe.util.ByteUtil;
import com.yollock.kobe.util.TransportUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

public class NettyEncoder extends MessageToMessageEncoder {

    private URL url;

    public NettyEncoder(URL url) {
        this.url = url;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        try {
            ByteBuf byteBuf = ctx.alloc().buffer();
            if (msg instanceof Request) {
                encodeRequest((Request) msg, byteBuf);
            } else if (msg instanceof Response) {
                encodeResponse((Response) msg, byteBuf);
            }
            out.add(byteBuf);
        } catch (Exception e) {
//            log.error("encode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
//            if (remotingCommand != null) {
//                log.error(remotingCommand.toString());
//            }
            e.fillInStackTrace();
            TransportUtil.closeChannel(ctx.channel());
        }
    }

    private void encodeRequest(Request request, ByteBuf out) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput output = new ObjectOutputStream(outputStream);
        output.writeUTF(request.getInterfaceName());
        output.writeUTF(request.getMethodName());
        output.writeUTF(request.getParamtersDesc());

        String serialize = url.getParameter(URLParam.serialize.getName(), URLParam.serialize.getValue());
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(serialize);
        if (serialization == null) {
            throw new KobeSerializeException("serialization is null");
        }

        if (request.getArguments() != null && request.getArguments().length > 0) {
            for (Object obj : request.getArguments()) {
                if (obj == null) {
                    output.writeObject(null);
                } else {
                    output.writeObject(serialization.serialize(obj));
                }
            }
        }

        if (request.getAttachments() == null || request.getAttachments().isEmpty()) {
            // empty attachments
            output.writeInt(0);
        } else {
            output.writeInt(request.getAttachments().size());
            for (Map.Entry<String, String> entry : request.getAttachments().entrySet()) {
                output.writeUTF(entry.getKey());
                output.writeUTF(entry.getValue());
            }
        }

        output.flush();
        byte[] body = outputStream.toByteArray();
        byte flag = Constants.OPAQUE_REQUEST;
        output.close();

        out.writeBytes(encode(flag, request.getId(), serialize));
        if (body != null) {
            out.writeBytes(body);
        }
    }

    private void encodeResponse(Response response, ByteBuf out) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput output = new ObjectOutputStream(outputStream);

        String serialize = url.getParameter(URLParam.serialize.getName(), URLParam.serialize.getValue());
        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(serialize);
        if (serialization == null) {
            throw new KobeSerializeException("serialization is null");
        }

        byte flag = Constants.OPAQUE_RESPONSE;

        output.writeLong(response.getProcessTime());

        if (response.getException() != null) {
            output.writeUTF(response.getException().getClass().getName());
            output.writeObject(serialization.serialize(response.getException()));
            flag |= Constants.OPAQUE_RESPONSE_EXCEPTION;
        } else if (response.getValue() == null) {
            flag |= Constants.OPAQUE_RESPONSE_VIOD;
        } else {
            output.writeUTF(response.getValue().getClass().getName());
            output.writeObject(serialization.serialize(response.getValue()));
            flag |= Constants.OPAQUE_RESPONSE_NORMAL;
        }

        output.flush();
        byte[] body = outputStream.toByteArray();
        output.close();

        out.writeBytes(encode(flag, response.getId(), serialize));
        if (body != null) {
            out.writeBytes(body);
        }
    }

    /**
     * 创建header
     *
     * @param flag
     * @param id
     * @return
     */
    private byte[] encode(byte flag, long id, String serialize) {
        byte[] header = new byte[Constants.NETTY_HEADER]; // TODO: 2017/1/13 静态化header长度值
        int offset = 0;

        // 0 - 1 bytes : magic
        ByteUtil.short2bytes(Constants.MAGIC, header, offset);
        offset += 2;

        // 4 - 7 bytes : rpc version
        ByteUtil.int2bytes(Version.CURRENT.getVersion(), header, offset);
        offset += 4;

        // 8 - 9 bytes : opaque
        ByteUtil.short2bytes(createOpaque(flag, serialize), header, offset);
        offset += 2;

        // 10 - 17 bytes : id
        ByteUtil.long2bytes(id, header, offset);
        // offset += 8;

        return header;
    }

    // TODO: 2017/1/17 完善opaque
    private short createOpaque(byte flag, String serialize) {
        short opaque = 0;

        // 组装请求和响应类型
        opaque |= flag;

        // 组装序列化方式
        if (Constants.FASTJSON.equals(serialize)) {
            opaque |= Constants.OPAQUE_FASTJSON;
        } else {
            opaque |= Constants.OPAQUE_HESSIAN2;
        }

        // oneway or towway
        if (!url.getBooleanParameter(URLParam.twoway.getName(), URLParam.twoway.getBooleanValue())) {
            opaque |= Constants.OPAQUE_ONEWAY;
        }

        return opaque;
    }

}
