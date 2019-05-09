package com.nix.jingxun.addp.ssh.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jingxun.zds
 */
@SpringBootApplication
@EnableSwagger2
public class SSHStart {

    public static void main(String[] args) {
        SpringApplication.run(SSHStart.class, args);
    }
}
