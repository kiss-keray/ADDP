package com.nix.jingxun.addp.rpc.common;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author keray
 * @date 2018/12/08 17:54
 */
public class AOPTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext application = new ClassPathXmlApplicationContext("classpath:rpc-springboot-aop.xml");

    }
}
