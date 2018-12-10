package com.nix.jingxun.addp.rpc.producer.test;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @author keray
 * @date 2018/12/08 20:10
 */
@Configuration
public class Con {

    @Bean
    public Hello hello() {
        return RPCConsumerFactory.consumer(Hello.class, 2000);
    }

}
