package com.nix.jingxun.addp.web.start.controller.router;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author keray
 * @date 2019/05/28 17:27
 */
@Controller
@RequestMapping("/#")
public class HashRouter {
    @GetMapping("/*")
    public String react() {
        return "/index";
    }
}
