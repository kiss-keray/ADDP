package com.nix.jingxun.addp.rpc.producer.netty;

import com.nix.jingxun.addp.rpc.common.RPCRemotingServer;
import com.nix.jingxun.addp.rpc.common.protocol.*;
import lombok.extern.slf4j.Slf4j;

/**
 * @author keray
 * @date 2018/10/19 2:27 PM
 */
@Slf4j
public class ProducerRemotingServer extends RPCRemotingServer {


    private ProducerRemotingServer(int port) {
        super(port, new ProducerServerIdleHandler());
    }

    @Override
    protected void doInit() {
        super.doInit();
        registerProcessor(ARPCProtocolV1.PROTOCOL_CODE, RPCPackageCode.RPC_INVOKE, new RPCInvokeProcessor());
    }

    public volatile static ProducerRemotingServer server;

    /**
     * server 单例
     */
    public static ProducerRemotingServer getServer(int port) {
        if (server == null) {
            synchronized (ProducerRemotingServer.class) {
                if (server == null) {
                    server = new ProducerRemotingServer(port);
                }
            }
        }
        return server;
    }
}
