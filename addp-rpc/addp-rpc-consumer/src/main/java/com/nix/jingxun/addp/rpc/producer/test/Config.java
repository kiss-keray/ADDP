package com.nix.jingxun.addp.rpc.producer.test;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import com.nix.jingxun.addp.rpc.producer.test.Hello;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2018/12/22 1:01
 */
@Configuration
public class Config {
    @Bean
    public Hello hello() {
        return RPCConsumerFactory.consumer(Hello.class);
    }
}
