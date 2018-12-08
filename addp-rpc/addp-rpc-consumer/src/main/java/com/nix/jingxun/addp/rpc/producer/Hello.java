package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.consumer.annotation.RPCConsumer;

/**
 * @author keray
 * @date 2018/12/08 16:36
 */
@RPCConsumer(appName = "",timeout = 1000)
public interface Hello {
    void sayHello(String str);
    String getHello();
}