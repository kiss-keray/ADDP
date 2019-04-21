package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.web.iservice.IMemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author keray
 * @date 2019/04/21 0:43
 */
@RestController("/member")
public class MemberController {
    @Resource
    private IMemberService memberService;

    @PostMapping("mapping")
    public Object login() {
        return null;
    }
}
