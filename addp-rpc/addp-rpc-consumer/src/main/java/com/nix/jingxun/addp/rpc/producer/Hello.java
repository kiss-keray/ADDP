package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;

/**
 * @author keray
 * @date 2018/12/08 16:36
 */
@RPCInterfaceAnnotation(appName = "app",timeout = 1000)
public interface Hello {
    void sayHello(String str);
    String getHello();
}