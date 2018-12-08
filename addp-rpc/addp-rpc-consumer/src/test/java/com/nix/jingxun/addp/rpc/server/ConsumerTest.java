package com.nix.jingxun.addp.rpc.server;
import com.nix.jingxun.addp.rpc.consumer.proxy.DynamicProxy;
import com.nix.jingxun.addp.rpc.producer.Hello;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author keray
 * @date 2018/12/07 16:48
 */
public class ConsumerTest{

    public static void main(String[] args) {
        ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("classpath:application.xml");

        InvocationHandler handler = application.getBean(DynamicProxy.class);
        Hello hello = (Hello) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{Hello.class}, handler);
        System.out.println(hello.getHello());
    }
}