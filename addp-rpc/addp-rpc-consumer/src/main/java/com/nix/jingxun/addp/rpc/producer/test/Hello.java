package com.nix.jingxun.addp.rpc.producer.test;

import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @author keray
 * @date 2018/12/08 16:36
 */
@RPCInterfaceAnnotation(appName = "app")
public interface Hello {
    void sayHello(String str);


    void sayHello1(List<String> strs);

    String getHello();

    User updateUser(User user, Boolean clear) throws TimeoutException,InterruptedException;
}