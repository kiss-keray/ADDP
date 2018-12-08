package com.nix.jingxun.addp.rpc.consumer.proxy;
import com.nix.jingxun.addp.rpc.common.RPCInterfaceAnnotation;
import com.nix.jingxun.addp.rpc.common.config.CommandCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author keray
 * @date 2018/12/08 19:56
 */
public class RPCConsumerFactory{
    private static DynamicProxy handler = new DynamicProxy();

    public static  <T> T consumer(Class<T> interfaceClazz) {
       return consumer(interfaceClazz,null);
    }
    public static  <T> T consumer(Class<T> interfaceClazz, long timeout, CommandCode type) {
        try {
            RPCInterfaceAnnotation annotation = interfaceClazz.getAnnotation(RPCInterfaceAnnotation.class);
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
            Field  declaredField = invocationHandler.getClass().getDeclaredField("memberValues");
            declaredField.setAccessible(true);
            ((Map) declaredField.get(invocationHandler)).put("timeout",timeout);
            if (type != null) {
                ((Map) declaredField.get(invocationHandler)).put("type", type);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{interfaceClazz}, handler);
    }
    public static <T> T consumer(Class<T> interfaceClazz,long timeout) {
        return consumer(interfaceClazz,timeout,null);
    }
    public static <T> T consumer(Class<T> interfaceClazz,CommandCode type) {
        return consumer(interfaceClazz,0,type);
    }
}
