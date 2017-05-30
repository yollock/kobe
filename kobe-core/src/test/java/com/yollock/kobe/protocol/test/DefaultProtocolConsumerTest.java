package com.yollock.kobe.protocol.test;

import com.alibaba.fastjson.JSON;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.protocol.DefaultProtocol;
import com.yollock.kobe.rpc.Consumer;
import com.yollock.kobe.rpc.DefaultRequest;
import com.yollock.kobe.rpc.Response;
import com.yollock.kobe.service.WaybillService;
import com.yollock.kobe.service.WaybillServiceImpl;
import com.yollock.kobe.util.ReflectUtil;

import java.lang.reflect.Method;

public class DefaultProtocolConsumerTest {

    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {

        DefaultProtocol protocol = new DefaultProtocol();
        URL url = new URL("kobe", "127.0.0.1", 8002, "com.yollock.kobe.service.WaybillService");
        url.addParameter("protocol", "kobe");
        url.addParameter("export", "8002");
        url.addParameter("serialization", "fastjson");
        Consumer consumer = protocol.consume(WaybillService.class, url, url);

        WaybillService waybillService = new WaybillServiceImpl();
        Method method = waybillService.getClass().getMethod("getById", String.class);

        DefaultRequest request = new DefaultRequest();
        request.setInterfaceName("com.yollock.kobe.service.WaybillService");
        request.setMethodName(method.getName());
        request.setParamtersDesc(ReflectUtil.getMethodParamDesc(method));
        request.setArguments(new Object[1]);

        Response response = consumer.invoke(request);
        System.out.println(JSON.toJSON(response));
    }

}
