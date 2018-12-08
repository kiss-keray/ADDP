package com.nix.jingxun.addp.rpc.producer;

import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 18:53
 */
public final class RPCProducer {
    public static void registerProducer(Object producer,String app,String group,String version) throws RuntimeException{
        InvokeContainer.addInterface(producer.getClass().getInterfaces()[0].getName(),producer);
    }
}
