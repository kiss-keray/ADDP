package com.nix.jingxun.addp.web;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jingxun.zds
 */
@SpringBootApplication(scanBasePackages = "com.nix.jingxun.addp.web.*")
@EnableSwagger2
@EnableMethodCache(basePackages = "com.nix.jingxun.addp.web.common.cache")
@EnableCreateCacheAnnotation
@EnableConfigurationProperties
public class WebStart {


    public static void main(String[] args) {
        SpringApplication.run(WebStart.class, args);
    }
}
