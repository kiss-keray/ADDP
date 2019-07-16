package com.nix.jingxun.addp.rpc.consumer;

import com.nix.jingxun.addp.rpc.consumer.proxy.RPCConsumerFactory;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2018/12/22 1:01
 */
@Configuration
public class Config {
    @Bean
    public IMemberService memberService() {
        return RPCConsumerFactory.consumer(IMemberService.class);
    }
}
