package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.producer.Hello;
import com.nix.jingxun.addp.rpc.producer.InvokeContainer;
import com.nix.jingxun.addp.rpc.producer.netty.NettyServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author keray
 * @date 2018/12/08 14:28
 */
public class Main {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("classpath:application.xml");
        InvokeContainer.addInterface(Hello.class.getName(),application.getBean(Hello.class));
        application.getBean(NettyServer.class).start();
    }
}
