package com.nix.jingxun.addp.web.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2019/05/13 16:38
 */
@Configuration
@ConfigurationProperties(prefix = "jingxun.web")
public  class WebConfig {
    public static String addpBaseFile;
}
