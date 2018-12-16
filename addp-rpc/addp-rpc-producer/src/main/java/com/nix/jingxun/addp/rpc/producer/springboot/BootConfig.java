package com.nix.jingxun.addp.rpc.producer.springboot;

import com.nix.jingxun.addp.rpc.producer.RegisterProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * @author keray
 * @date 2018/12/10 18:25
 */
@ConfigurationProperties(prefix = "jingxun")
public class BootConfig {
    @PostConstruct
    public void init() {
    }

    private String localhost;
    private int port;

    public String getLocalhost() {
        return localhost;
    }

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
        System.setProperty("rpc.producer.server.host",localhost);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        System.setProperty("rpc.producer.server.port", String.valueOf(port));
    }
    @Bean
    public RegisterProducer autoProducer() {
        return new RegisterProducer();
    }
}
