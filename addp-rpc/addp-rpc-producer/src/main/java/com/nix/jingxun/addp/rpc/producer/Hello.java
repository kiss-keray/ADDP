package com.nix.jingxun.addp.rpc.producer;

import org.springframework.stereotype.Component;

/**
 * @author keray
 * @date 2018/12/07 22:42
 */
@Component
public interface Hello {
    void sayHello(String str);
    String getHello();
}
