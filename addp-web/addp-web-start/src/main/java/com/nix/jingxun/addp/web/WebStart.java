package com.nix.jingxun.addp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jingxun.zds
 */
@SpringBootApplication(scanBasePackages = "com.nix.jingxun.addp.web.*")
@EnableSwagger2
public class WebStart {


    public static void main(String[] args) {
        SpringApplication.run(WebStart.class, args);
    }
}
