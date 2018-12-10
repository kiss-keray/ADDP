package com.nix.jingxun.addp.rpc.common.config;

/**
 * @author keray
 * @date 2018/12/09 20:01
 */

public class CommonConfig {
    public final static int SERVER_PORT = Integer.parseInt(System.getProperty("rpc.server.port", "15100"));
    public final static String SERVER_HOST = System.getProperty("rpc.producer.server.host", "127.0.0.1") + ":" + SERVER_PORT;
    public final static int PRODUCER_INVOKE_PORT = Integer.parseInt(System.getProperty("rpc.producer.server.port", "15000"));
    public final static String PRODUCER_INVOKE_LOCALHOST = System.getProperty("rpc.producer.server.host", null);
}
