package com.nix.jingxun.addp.rpc.producer;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.jws.soap.SOAPBinding;

/**
 * @author keray
 * @date 2018/12/07 23:23
 */
@Data
@AllArgsConstructor
public class User {
    private String username;
    private int age;
    User child;
}
