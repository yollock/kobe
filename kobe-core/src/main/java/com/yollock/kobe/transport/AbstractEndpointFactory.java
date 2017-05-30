package com.yollock.kobe.transport;

import com.yollock.kobe.common.URL;
import com.yollock.kobe.rpc.Provider;
import com.yollock.kobe.util.KobeUtil;
import com.yollock.kobe.util.LoggerUtil;
import com.yollock.kobe.util.ReflectUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class AbstractEndpointFactory implements EndpointFactory {

    protected Map<String, Server> ipPort2ServerShareChannel = new HashMap<String, Server>();
    protected ConcurrentMap<Server, Set<String>> server2UrlsShareChannel = new ConcurrentHashMap<Server, Set<String>>();

    private EndpointManager heartbeatManager = null;

    public AbstractEndpointFactory() {
        heartbeatManager = new HeartbeatManager();
        heartbeatManager.init();
    }

    @Override
    public Server createServer(URL url, Map<String, Provider<?>> provivers) {
        synchronized (ipPort2ServerShareChannel) {
            String ipPort = url.getServerPortStr();
            String protocolKey = KobeUtil.getProtocolKey(url);

            boolean shareChannel = false; // url.getBooleanParameter(URLParamType.shareChannel.getName(), URLParamType.shareChannel.getBooleanValue());

            if (!shareChannel) { // 独享一个端口
//                LoggerUtil.info(ReflectUtil.simpleClassName(this) + " create no_share_channel server: url={}", url);
                // 如果端口已经被使用了，使用该server bind 会有异常
                return subCreateServer(url, provivers);
            }

            LoggerUtil.info(ReflectUtil.simpleClassName(this) + " create share_channel server: url={}", url);

            Server server = ipPort2ServerShareChannel.get(ipPort);

            if (server != null) {
                // can't share service channel
//                if (!MotanFrameworkUtil.checkIfCanShallServiceChannel(server.getUrl(), url)) {
//                    throw new MotanFrameworkException("Service export Error: share channel but some config param is different, protocol or codec or serialize or maxContentLength or maxServerConnection or maxWorkerThread or heartbeatFactory, source=" + server.getUrl() + " target=" + url, MotanErrorMsgConstant.FRAMEWORK_EXPORT_ERROR);
//                }
                saveEndpoint2Urls(server2UrlsShareChannel, server, protocolKey);
                return server;
            }

            url = url.createCopy();
            url.setPath(""); // 共享server端口，由于有多个interfaces存在，所以把path设置为空

            server = subCreateServer(url, provivers);

            ipPort2ServerShareChannel.put(ipPort, server);
            saveEndpoint2Urls(server2UrlsShareChannel, server, protocolKey);

            return server;
        }
    }

    private <T> void saveEndpoint2Urls(ConcurrentMap<T, Set<String>> map, T endpoint, String namespace) {
        Set<String> sets = map.get(endpoint);

        if (sets == null) {
            sets = new HashSet<String>();
            sets.add(namespace);
            map.putIfAbsent(endpoint, sets); // 规避并发问题，因为有release逻辑存在，所以这里的sets预先add了namespace
            sets = map.get(endpoint);
        }

        sets.add(namespace);
    }

    @Override
    public Client createClient(URL url) {
//        LoggerUtil.info(ReflectUtil.simpleClassName(this) + " create client: url={}", url);
        return createClient(url, heartbeatManager);
    }

    private Client createClient(URL url, EndpointManager endpointManager) {
        Client client = subCreateClient(url);
        endpointManager.addEndpoint(client);
        return client;
    }

    @Override
    public void closeServer(Server server, URL url) {

    }

    @Override
    public void closeClient(Client client, URL url) {
//        if (endpoint instanceof Client) {
//            endpoint.close();
//            heartbeatClientEndpointManager.removeEndpoint(endpoint);
//        } else {
//            endpoint.close();
//        }
    }

    public EndpointManager getHeartbeatManager() {
        return heartbeatManager;
    }

    public void setHeartbeatManager(EndpointManager heartbeatManager) {
        this.heartbeatManager = heartbeatManager;
    }

    protected abstract Server subCreateServer(URL url, Map<String, Provider<?>> provivers);

    protected abstract Client subCreateClient(URL url);
}
