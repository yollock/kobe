package com.yollock.kobe.protocol.test;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.protocol.DefaultProtocol;
import com.yollock.kobe.service.WaybillService;
import com.yollock.kobe.service.WaybillServiceImpl;

public class DefaultProtocolProviderTest {

    public static void main(String[] args) throws InterruptedException {

        DefaultProtocol protocol = new DefaultProtocol();
        WaybillService service = new WaybillServiceImpl();
        URL url = new URL("kobe", "127.0.0.1", 8002, "com.yollock.kobe.service.WaybillService");
        url.addParameter("protocol", "kobe");
        url.addParameter("export", "8002");
        url.addParameter("serialization", "fastjson");
        protocol.provide(service, url);

    }

}
