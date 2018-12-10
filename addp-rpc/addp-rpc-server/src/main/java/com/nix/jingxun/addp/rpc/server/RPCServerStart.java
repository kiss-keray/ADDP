package com.nix.jingxun.addp.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jingxun.zds
 */
@SpringBootApplication
@ComponentScan("com.nix.jingxun.addp.*")
public class RPCServerStart {
    public static void main(String[] args) {
        SpringApplication.run(RPCServerStart.class, args);
    }
}