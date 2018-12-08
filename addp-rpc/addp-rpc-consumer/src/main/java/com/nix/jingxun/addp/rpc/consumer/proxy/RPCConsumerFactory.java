package com.nix.jingxun.addp.rpc.consumer.proxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * @author keray
 * @date 2018/12/08 19:56
 */
@Component
public class RPCConsumerFactory {
    @Autowired
    private DynamicProxy handler;

    public  <T> T consumer(Class<T> interfaceClazz) {
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{interfaceClazz}, handler);
    }

}
