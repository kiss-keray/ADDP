package com.nix.jingxun.addp.rpc.producer;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2018/12/08 20:10
 */
@Configuration
public class Con {
    @Autowired
    private RPCConsumerFactory consumerFactory;

    @Bean
    public Hello hello() {
        return consumerFactory.consumer(Hello.class);
    }

}
