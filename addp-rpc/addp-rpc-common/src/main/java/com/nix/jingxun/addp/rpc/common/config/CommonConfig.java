package com.nix.jingxun.addp.rpc.common.config;

import lombok.extern.slf4j.Slf4j;


/**
 * @author keray
 * @date 2018/12/09 20:01
 */
@Slf4j
public class CommonConfig {
    public final static int SERVER_PORT =
            Integer.parseInt(System.getProperty("rpc.server.port", "15100"));
    public final static String SERVER_HOST =
            System.getProperty("rpc.server.host", "127.0.0.1") + ":" + SERVER_PORT;
    public final static int PRODUCER_INVOKE_PORT =
            Integer.parseInt(System.getProperty("rpc.producer.port", "15000"));
    public final static String PRODUCER_INVOKE_LOCALHOST =
            System.getProperty("rpc.producer.host", null);
    public final static Boolean NO_CENTER_SERVER =
            Boolean.valueOf(System.getProperty("rpc.producer.server.no", "false"));
    public final static String[] STATIC_PRODUCER_HOST = System.getProperty("rpc.producer.servers", "").split(",");

    /**
     * check config
     * */
    static {
        if (NO_CENTER_SERVER && STATIC_PRODUCER_HOST[0].length() == 0) {
            log.warn("去掉注册中心时必须设置服务方ip");
        }
    }
}
