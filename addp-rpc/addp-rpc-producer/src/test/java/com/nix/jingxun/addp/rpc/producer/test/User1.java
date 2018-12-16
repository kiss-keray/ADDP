package com.nix.jingxun.addp.rpc.producer.test;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author keray
 * @date 2018/12/07 23:23
 */
@Data
@AllArgsConstructor
public class User1 {
    private String username;
    private int age;
    User1 child;
}
