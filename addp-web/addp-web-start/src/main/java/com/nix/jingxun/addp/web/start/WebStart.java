package com.nix.jingxun.addp.web.start;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jingxun.zds
 */
@SpringBootApplication
@MapperScan(basePackages = {"dal"})
public class WebStart {

    public static void main(String[] args) {
        SpringApplication.run(WebStart.class, args);
    }
}
