package com.nix.jingxun.addp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jingxun.zds
 */
@SpringBootApplication(scanBasePackages = "com.nix.jingxun.addp.web.*")
@EnableSwagger2
@EnableConfigurationProperties
@EnableScheduling
public class WebStart {


    public static void main(String[] args) {
        SpringApplication.run(WebStart.class, args);
    }
}
