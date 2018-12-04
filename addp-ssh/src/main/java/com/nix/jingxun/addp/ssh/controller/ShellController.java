package com.nix.jingxun.addp.ssh.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author keray
 * @date 2018/12/04 下午3:06
 */
@RestController("/shell")
public class ShellController {

    @GetMapping("/create")
    public boolean createShell(String ip,String username,String password) {
        return false;
    }
}
