package com.yollock.kobe.common;

import java.util.regex.Pattern;

/**
 * Created by yollock on 2016/12/27.
 */
public abstract class Constants {

    public static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static final String SEPERATOR_ACCESS_LOG = "|";
    public static final String COMMA_SEPARATOR = ",";
    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    public static final String PROTOCOL_SEPARATOR = "://";
    public static final String PATH_SEPARATOR = "/";
    public static final String REGISTRY_SEPARATOR = "|";
    public static final Pattern REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|;]+\\s*");
    public static final String SEMICOLON_SEPARATOR = ";";
    public static final Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");
    public static final String QUERY_PARAM_SEPARATOR = "&";
    public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("\\s*[&]+\\s*");
    public static final String EQUAL_SIGN_SEPERATOR = "=";
    public static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("\\s*[=]\\s*");
    public static final String NODE_TYPE_SERVICE = "service";
    public static final String NODE_TYPE_REFERER = "referer";
    public static final String SCOPE_NONE = "none";
    public static final String SCOPE_LOCAL = "local";
    public static final String SCOPE_REMOTE = "remote";
    public static final String REGISTRY_PROTOCOL_LOCAL = "local";
    public static final String REGISTRY_PROTOCOL_DIRECT = "direct";
    public static final String REGISTRY_PROTOCOL_ZOOKEEPER = "zookeeper";
    public static final String PROTOCOL_INJVM = "injvm";
    public static final String PROTOCOL_MOTAN = "motan";
    public static final String PROXY_JDK = "jdk";
    public static final String PROXY_JAVASSIST = "javassist";
    public static final String FRAMEWORK_NAME = "kobe";
    public static final String PROTOCOL_SWITCHER_PREFIX = "protocol:";
    public static final String METHOD_CONFIG_PREFIX = "methodconfig.";
    public static final int MILLS = 1;
    public static final int SECOND_MILLS = 1000;
    public static final int MINUTE_MILLS = 60 * SECOND_MILLS;
    public static final String DEFAULT_VALUE = "default";
    public static final int DEFAULT_INT_VALUE = 0;
    public static final String DEFAULT_VERSION = "1.0";
    public static final boolean DEFAULT_THROWS_EXCEPTION = true;
    public static final String DEFAULT_CHARACTER = "utf-8";
    public static final int SLOW_COST = 50; // 50ms
    public static final int STATISTIC_PEROID = 30; // 30 seconds


    /**
     * opaque
     */
    public static final short OPAQUE_REQUEST = 0;  // 00000000 00000000
    public static final short OPAQUE_RESPONSE = 1; // 00000000 00000001
    public static final short OPAQUE_RESPONSE_NORMAL = 0; // 00000000 00000000
    public static final short OPAQUE_RESPONSE_VIOD = 1 << 2; // 00000000 00000010
    public static final short OPAQUE_RESPONSE_EXCEPTION = 1 << 3; // 00000000 00000100
    public static final short OPAQUE_TWOWAY = 0;      // 00000000 00000000
    public static final short OPAQUE_ONEWAY = 1 << 4; // 00000000 00001000
    public static final short OPAQUE_HESSIAN2 = 0; // 00000000 00000000
    public static final short OPAQUE_FASTJSON = 1 << 5; // 00000000 00010000


    /**
     * serialize
     */
    public static final String SERIAL_NULL = "";
    public static final String HESSIAN2 = "hessian2";
    public static final String FASTJSON = "fastjson";


    /**
     * netty channel constants start
     **/

    // protocol magic number
    public static final short MAGIC = (short) 0xF0F0;
    // netty codec
    public static final short NETTY_MAGIC_TYPE = (short) 0xF1F1;
    // netty header length
    public static final int NETTY_HEADER = 16;
    // netty server max excutor thread
    public static final int NETTY_EXECUTOR_MAX_SIZE = 800;
    // netty thread idle time: 1 mintue
    public static final int NETTY_THREAD_KEEPALIVE_TIME = 60 * 1000;
    // netty client max concurrent request TODO 2W is suitable?
    public static final int NETTY_CLIENT_MAX_REQUEST = 20000;
    // share channel max worker thread
    public static final int NETTY_SHARECHANNEL_MAX_WORKDER = 800;
    // share channel min worker thread
    public static final int NETTY_SHARECHANNEL_MIN_WORKDER = 40;
    // don't share channel max worker thread
    public static final int NETTY_NOT_SHARECHANNEL_MAX_WORKDER = 200;
    // don't share channel min worker thread
    public static final int NETTY_NOT_SHARECHANNEL_MIN_WORKDER = 20;
    public static final int NETTY_TIMEOUT_TIMER_PERIOD = 100;


    /**
     * heartbeat constants start
     */
    public static final int HEARTBEAT_PERIOD = 500;
    public static final String HEARTBEAT_INTERFACE_NAME = "com.weibo.api.motan.rpc.heartbeat";
    public static final String HEARTBEAT_METHOD_NAME = "heartbeat";
    public static final String HHEARTBEAT_PARAM = "void";
    /**
     * heartbeat constants end
     */

    public static final String ZOOKEEPER_REGISTRY_NAMESPACE = "/kobe";

    public static final String REGISTRY_HEARTBEAT_SWITCHER = "feature.configserver.heartbeat";

    /**
     * 默认的consistent的hash的数量
     */
    public static final int DEFAULT_CONSISTENT_HASH_BASE_LOOP = 1000;

    // consumer / provider
    public static final long CONSUMER_DESTROY_DELAY_TIME = 1000; // 正常情况下请求超过1s已经是能够忍耐的极限值了，delay 1s进行destroy

    private Constants() {
    }
}
