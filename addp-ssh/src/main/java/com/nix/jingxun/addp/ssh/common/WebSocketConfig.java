package com.nix.jingxun.addp.ssh.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author keray
 * @date 2018/12/04 下午7:24
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter  serverEndpointExporter(){
        return new ServerEndpointExporter();
    }
}
