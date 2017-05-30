package com.yollock.kobe.util;

import com.google.common.base.Objects;
import com.yollock.kobe.common.Constants;
import com.yollock.kobe.common.URL;
import com.yollock.kobe.common.URLParam;
import com.yollock.kobe.rpc.Request;

public abstract class KobeUtil {

    /**
     * 获取反序列化标识
     *
     * @param opaque
     * @return
     */
    public static String getSerializeType(short opaque) {
        if ((Constants.OPAQUE_FASTJSON & opaque) == Constants.OPAQUE_FASTJSON) {
            return Constants.FASTJSON;
        } else {
            return Constants.HESSIAN2;
        }
    }

    /**
     * 目前根据 group/interface/version 来唯一标示一个服务
     *
     * @param request
     * @return
     */

    public static String getServiceKey(Request request) {
        String version = getVersionFromRequest(request);
        String group = getGroupFromRequest(request);

        return getServiceKey(group, request.getInterfaceName(), version);
    }

    public static String getGroupFromRequest(Request request) {
        return getValueFromRequest(request, URLParam.group.name(), URLParam.group.getValue());
    }

    public static String getVersionFromRequest(Request request) {
        return getValueFromRequest(request, URLParam.version.name(), URLParam.version.getValue());
    }

    public static String getValueFromRequest(Request request, String key, String defaultValue) {
        String value = defaultValue;
        if (request.getAttachments() != null && request.getAttachments().containsKey(key)) {
            value = request.getAttachments().get(key);
        }
        return value;
    }

    /**
     * 目前根据 group/interface/version 来唯一标示一个服务
     *
     * @param url
     * @return
     */
    public static String getServiceKey(URL url) {
        return getServiceKey(url.getGroup(), url.getPath(), url.getVersion());
    }

    /**
     * protocol key: protocol://host:port/group/interface/version
     *
     * @param url
     * @return
     */
    public static String getProtocolKey(URL url) {
        return url.getProtocol() + Constants.PROTOCOL_SEPARATOR + url.getServerPortStr() + Constants.PATH_SEPARATOR + url.getGroup() + Constants.PATH_SEPARATOR + url.getPath() + Constants.PATH_SEPARATOR + url.getVersion();
    }

    /**
     * 输出请求的关键信息： requestId=** interface=** method=**(**)
     *
     * @param request
     * @return
     */
    public static String toString(Request request) {
        return "requestId=" + request.getId() + " interface=" + request.getInterfaceName() + " method=" + request.getMethodName() + "(" + request.getParamtersDesc() + ")";
    }

    /**
     * 根据Request得到 interface.method(paramDesc) 的 desc
     * <p/>
     * <pre>
     * 		比如：
     * 			package com.weibo.api.motan;
     *
     * 		 	interface A { public hello(int age); }
     *
     * 			那么return "com.weibo.api.motan.A.hell(int)"
     * </pre>
     *
     * @param request
     * @return
     */
    public static String getFullMethodString(Request request) {
        return request.getInterfaceName() + "." + request.getMethodName() + "(" + request.getParamtersDesc() + ")";
    }

    public static String getGroupMethodString(Request request) {
        return getGroupFromRequest(request) + "_" + getFullMethodString(request);
    }


    /**
     * 判断url:source和url:target是否可以使用共享的service channel(port) 对外提供服务
     * <p/>
     * <pre>
     * 		1） protocol
     * 		2） codec
     * 		3） serialize
     * 		4） maxContentLength
     * 		5） maxServerConnection
     * 		6） maxWorkerThread
     * 		7） workerQueueSize
     * 		8） heartbeatFactory
     * </pre>
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean checkIfCanShallServiceChannel(URL source, URL target) {
        if (!Objects.equal(source.getProtocol(), target.getProtocol())) {
            return false;
        }

//        if (!Objects.equal(source.getParameter(URLParam.codec.getName()), target.getParameter(URLParam.codec.getName()))) {
//            return false;
//        }

        if (!Objects.equal(source.getParameter(URLParam.serialize.getName()), target.getParameter(URLParam.serialize.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.maxContentLength.getName()), target.getParameter(URLParam.maxContentLength.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.maxServerConnection.getName()), target.getParameter(URLParam.maxServerConnection.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.maxWorkerThread.getName()), target.getParameter(URLParam.maxWorkerThread.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.workerQueueSize.getName()), target.getParameter(URLParam.workerQueueSize.getName()))) {
            return false;
        }

        return Objects.equal(source.getParameter(URLParam.heartbeatFactory.getName()), target.getParameter(URLParam.heartbeatFactory.getName()));

    }

    /**
     * 判断url:source和url:target是否可以使用共享的client channel(port) 对外提供服务
     * <p/>
     * <pre>
     * 		1） protocol
     * 		2） codec
     * 		3） serialize
     * 		4） maxContentLength
     * 		5） maxClientConnection
     * 		6） heartbeatFactory
     * </pre>
     *
     * @param source
     * @param target
     * @return
     */
    public static boolean checkIfCanShallClientChannel(URL source, URL target) {
        if (!Objects.equal(source.getProtocol(), target.getProtocol())) {
            return false;
        }

//        if (!Objects.equal(source.getParameter(URLParam.codec.getName()), target.getParameter(URLParam.codec.getName()))) {
//            return false;
//        }

        if (!Objects.equal(source.getParameter(URLParam.serialize.getName()), target.getParameter(URLParam.serialize.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.maxContentLength.getName()), target.getParameter(URLParam.maxContentLength.getName()))) {
            return false;
        }

        if (!Objects.equal(source.getParameter(URLParam.maxClientConnection.getName()), target.getParameter(URLParam.maxClientConnection.getName()))) {
            return false;
        }

        return Objects.equal(source.getParameter(URLParam.heartbeatFactory.getName()), target.getParameter(URLParam.heartbeatFactory.getName()));

    }

    /**
     * serviceKey: group/interface/version
     *
     * @param group
     * @param interfaceName
     * @param version
     * @return
     */
    private static String getServiceKey(String group, String interfaceName, String version) {
        return group + Constants.PATH_SEPARATOR + interfaceName + Constants.PATH_SEPARATOR + version;
    }

    /**
     * 获取默认motan协议配置
     *
     * @return motan协议配置
     */
//    public static ProtocolConfig getDefaultProtocolConfig() {
//        ProtocolConfig pc = new ProtocolConfig();
//        pc.setId("motan");
//        pc.setName("motan");
//        return pc;
//    }

    /**
     * 默认本地注册中心
     *
     * @return local registry
     */
//    public static RegistryConfig getDefaultRegistryConfig() {
//        RegistryConfig local = new RegistryConfig();
//        local.setRegProtocol("local");
//        return local;
//    }
    private KobeUtil() {
    }
}
