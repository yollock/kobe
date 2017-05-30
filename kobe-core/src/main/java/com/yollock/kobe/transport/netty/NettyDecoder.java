package com.yollock.kobe.transport.netty;

import com.yollock.kobe.common.Constants;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.common.exception.KobeSerializeException;
import com.yollock.kobe.common.exception.KobeTransportException;
import com.yollock.kobe.common.extension.ExtensionLoader;
import com.yollock.kobe.rpc.DefaultRequest;
import com.yollock.kobe.rpc.DefaultResponse;
import com.yollock.kobe.serialize.FastJsonSerialization;
import com.yollock.kobe.serialize.Serialization;
import com.yollock.kobe.transport.EndpointFactory;
import com.yollock.kobe.util.ByteUtil;
import com.yollock.kobe.util.KobeUtil;
import com.yollock.kobe.util.ReflectUtil;
import com.yollock.kobe.util.TransportUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NettyDecoder extends ByteToMessageDecoder {

    private static final int FRAME_MAX_LENGTH = Integer.parseInt(System.getProperty("kobe.transport.frameMaxLength", "8388608"));

    private URL url;

    public NettyDecoder(URL url) {
        this.url = url;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            final int length = in.readableBytes();
            if (length <= Constants.NETTY_HEADER) {
                return;
            }
            in.markReaderIndex();

            short magic = in.readShort();
            if (magic != Constants.MAGIC) {
                in.resetReaderIndex();
                throw new KobeTransportException("NettyDecoder transport header not support, magic: " + magic);
            }

            int protocolVersion = in.readInt();
            short opaque = in.readShort();
            long requestId = in.readLong();

            byte[] body = new byte[length - 16];
            in.readBytes(body);

            out.add(doDecode(url, body, opaque, requestId));
        } catch (Exception e) {
//            log.error("decode exception, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()), e);
            in.markReaderIndex();
            TransportUtil.closeChannel(ctx.channel());
            e.printStackTrace();
            throw new KobeTransportException("解析失败" + e.getMessage());
        }
    }

    private Object doDecode(URL url, byte[] bodyData, short opaque, long requestId) throws IOException, ClassNotFoundException {
        if ((Constants.OPAQUE_RESPONSE & opaque) == 0) {
            return doDecodeRequest(url, bodyData, opaque, requestId);
        } else {
            return doDecodeResponse(url, bodyData, opaque, requestId);
        }
    }

    private Object doDecodeResponse(URL url, byte[] bodyData, short opaque, long requestId) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bodyData);
        ObjectInput input = new ObjectInputStream(inputStream);

        long processTime = input.readLong();
        DefaultResponse response = new DefaultResponse();
        response.setId(requestId);
        response.setProcessTime(processTime);

        if ((Constants.OPAQUE_RESPONSE_VIOD & opaque) == Constants.OPAQUE_RESPONSE_VIOD) {
            return response;
        }
        String className = input.readUTF();
        Class<?> clz = ReflectUtil.forName(className);

        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(KobeUtil.getSerializeType(opaque));
        if (serialization == null) {
            throw new KobeSerializeException("serialization is null");
        }

        Object result = null;
        final Object resultObject = input.readObject();
        if (resultObject != null) {
            result = serialization.deserialize((byte[]) resultObject, clz);
        }

        if ((Constants.OPAQUE_RESPONSE_NORMAL & opaque) == Constants.OPAQUE_RESPONSE_NORMAL) {
            response.setValue(result);
        } else if ((Constants.OPAQUE_RESPONSE_EXCEPTION & opaque) == Constants.OPAQUE_RESPONSE_EXCEPTION) {
            response.setException((Exception) result);
        } else {
            throw new KobeTransportException("decode error: response dataType not support ");
        }

        response.setId(requestId);
        input.close();
        return response;
    }

    private Object doDecodeRequest(URL url, byte[] bodyData, short opaque, long requestId) throws IOException, ClassNotFoundException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bodyData);
        ObjectInput input = new ObjectInputStream(inputStream);

        String interfaceName = input.readUTF();
        String methodName = input.readUTF();
        String paramtersDesc = input.readUTF();

        Serialization serialization = ExtensionLoader.getExtensionLoader(Serialization.class).getExtension(KobeUtil.getSerializeType(opaque));
        if (serialization == null) {
            throw new KobeSerializeException("serialization is null");
        }

        DefaultRequest request = new DefaultRequest();
        request.setId(requestId);
        request.setInterfaceName(interfaceName);
        request.setMethodName(methodName);
        request.setParamtersDesc(paramtersDesc);
        request.setArguments(decodeRequestParameter(input, paramtersDesc, serialization));
        request.setAttachments(decodeRequestAttachments(input));

        input.close();
        return request;
    }

    private Object[] decodeRequestParameter(ObjectInput input, String parameterDesc, Serialization serialization) throws IOException, ClassNotFoundException {
        if (parameterDesc == null || parameterDesc.equals("")) {
            return null;
        }

        Class<?>[] classTypes = ReflectUtil.forNames(parameterDesc);

        Object[] paramObjs = new Object[classTypes.length];
        for (int i = 0; i < classTypes.length; i++) {
            final Class value = classTypes[i];
            if (value == null) {
                continue;
            }
            byte[] param = (byte[]) input.readObject();
            if (param == null) {
                paramObjs[i] = null;
                continue;
            }
            paramObjs[i] = serialization.deserialize((byte[]) param, classTypes[i]);
        }

        return paramObjs;
    }

    private Map<String, String> decodeRequestAttachments(ObjectInput input) throws IOException, ClassNotFoundException {
        int size = input.readInt();

        if (size <= 0) {
            return null;
        }

        Map<String, String> attachments = new HashMap<String, String>();
        for (int i = 0; i < size; i++) {
            attachments.put(input.readUTF(), input.readUTF());
        }

        return attachments;
    }

}

