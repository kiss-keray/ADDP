package com.nix.jingxun.addp.web.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author keray
 * @date 2019/06/05 22:40
 */

@Configuration
@ConfigurationProperties(prefix = "jingxun.web.mq")
public class MQConfig {

    public static String rocketMQHost;
    public static String mqGroup;
    public static String mqInstanceName;

    public void setRocketMQHost(String rocketMQHost) {
        MQConfig.rocketMQHost = rocketMQHost;
    }

    public void setMqGroup(String mqGroup) {
        MQConfig.mqGroup = mqGroup;
    }

    public void setMqInstanceName(String mqInstanceName) {
        MQConfig.mqInstanceName = mqInstanceName;
    }
}
