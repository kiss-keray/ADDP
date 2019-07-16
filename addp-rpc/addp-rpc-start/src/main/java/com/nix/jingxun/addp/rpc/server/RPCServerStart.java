package com.nix.jingxun.addp.rpc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jingxun.zds
 */
@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties
@EnableScheduling
public class RPCServerStart {
    public static void main(String[] args) {
        SpringApplication.run(RPCServerStart.class, args);
    }
}
