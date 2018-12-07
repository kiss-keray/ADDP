package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.consumer.annotation.RPCConsumer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author keray
 * @date 2018/12/07 16:48
 */
@RPCConsumer(appName = "test")
@Component
public class ConsumerTest{
    @PostConstruct
    public void init() {
        System.out.println("init");
    }
}