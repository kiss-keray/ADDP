package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.producer.netty.ProducerRemotingServer;

/**
 * @author keray
 * @date 2018/12/07 18:53
 */
public final class RPCProducer {
    private static final int PORT = Integer.parseInt(System.getProperty("rpc.server.port","15000"));
    private static ProducerRemotingServer nettyServer = ProducerRemotingServer.getServer(PORT);
    static {
        nettyServer.start();
    }
    public static void registerProducer(Object producer,String app,String group,String version) throws RuntimeException{

        InvokeContainer.addInterface(producer.getClass().getInterfaces()[0].getName(),producer);
    }
}
