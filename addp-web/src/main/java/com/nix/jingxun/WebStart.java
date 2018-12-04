package com.nix.jingxun;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author jingxun.zds
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.nix.jingxun.addp.dal"})
public class WebStart {

    public static void main(String[] args) {
        SpringApplication.run(WebStart.class, args);
    }
}
