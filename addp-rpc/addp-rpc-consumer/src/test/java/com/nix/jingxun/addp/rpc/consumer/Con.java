package com.nix.jingxun.addp.rpc.consumer;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import com.rpc.interfaces.Hello;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author keray
 * @date 2018/12/08 20:10
 */
@Configuration
public class Con {
    @Autowired
    ApplicationContext applicationContext;

    @Bean
    public Hello hello() {
        Hello hello = RPCConsumerFactory.consumer(Hello.class, 2000);
        System.out.println("hello=" + hello.getClass().getClassLoader());
        return hello;
    }
    @Bean
    public com.nix.jingxun.addp.rpc.producer.test.Hello hello1() {
        return RPCConsumerFactory.consumer(com.nix.jingxun.addp.rpc.producer.test.Hello.class, 2000);
    }

}
