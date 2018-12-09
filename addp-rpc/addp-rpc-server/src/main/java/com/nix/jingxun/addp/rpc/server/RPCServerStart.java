package com.nix.jingxun.addp.rpc.server;

import com.nix.jingxun.addp.rpc.server.netty.ServerRemotingServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.PropertySource;

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