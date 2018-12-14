package com.nix.jingxun.addp.rpc.producer.test;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.common.RPCType;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/12/15 00:50
 */
@RPCInterfaceAnnotation(appName = "app",
        group = "RPC",
        version = "1.0.0",
        timeout = 10000,
        type = RPCType.SYNC_EXEC_METHOD)
public class HelloImpl1 implements Hello{
    @Override
    public void sayHello(String str) {

    }

    @Override
    public void sayHello1(List<String> strs) {

    }

    @Override
    public String getHello() {
        return null;
    }

    @Override
    public User updateUser(User var1, Boolean var2)  throws TimeoutException,InterruptedException{
        return null;
    }
}
