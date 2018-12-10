package com.nix.jingxun.addp.rpc.producer.test;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;

/**
 * @author keray
 * @date 2018/12/07 22:42
 */
@RPCInterfaceAnnotation(appName = "app")
public interface Hello {
    void sayHello(String str);

    String getHello();
}
