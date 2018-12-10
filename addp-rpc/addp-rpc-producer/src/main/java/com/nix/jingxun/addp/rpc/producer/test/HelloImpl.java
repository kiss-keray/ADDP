package com.nix.jingxun.addp.rpc.producer.test;

import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 22:42
 */
@Component
public class HelloImpl implements Hello {

    @Override
    public void sayHello(String str) {
        System.out.println(str);
    }

    @Override
    public String getHello() {
        return "hello world";
    }
}
