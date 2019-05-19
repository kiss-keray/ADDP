package com.nix.jingxun.addp.web.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2019/05/13 16:38
 */
@Configuration
@ConfigurationProperties(prefix = "jingxun.web")
@Data
public  class WebConfig {
    public static String addpBaseFile;

    public static String aesKey;

    public void setAddpBaseFile(String addpBaseFile) {
        WebConfig.addpBaseFile = addpBaseFile;
    }

    public void setAesKey(String aesKey) {
        WebConfig.aesKey = aesKey;
    }
}
