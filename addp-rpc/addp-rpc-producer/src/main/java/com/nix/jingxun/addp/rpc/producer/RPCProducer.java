package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.producer.netty.NettyServer;
import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 18:53
 */
public final class RPCProducer {
    private static  NettyServer nettyServer = new NettyServer();
    static {
        nettyServer.start();
    }
    public static void registerProducer(Object producer,String app,String group,String version) throws RuntimeException{
        InvokeContainer.addInterface(producer.getClass().getInterfaces()[0].getName(),producer);
    }
}
