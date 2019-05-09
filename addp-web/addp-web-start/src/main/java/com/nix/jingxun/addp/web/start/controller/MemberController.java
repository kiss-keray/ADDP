package com.nix.jingxun.addp.web.start.controller;

import com.nix.jingxun.addp.common.Result;
import com.nix.jingxun.addp.web.common.cache.MemberCache;
import com.nix.jingxun.addp.web.iservice.IMemberService;
import com.nix.jingxun.addp.web.model.MemberModel;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author keray
 * @date 2019/04/21 0:43
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Resource
    private IMemberService memberService;

    @PostMapping("register")
    public Result register(@Valid @ModelAttribute MemberModel memberModel) {
        return Result.of(() -> memberService.register(memberModel)).flatMap(member -> {
            if (member == null) {
                return Result.fail("FAIL","注册失败");
            } else if (member.getId() == null) {
                return Result.fail("FAIL","用户已存在");
            }
            MemberCache.setCurrentUser(member);
            return Result.success(member);
        }).logFail();
    }

    @PostMapping("/login")
    public Result login(@RequestParam("username") String username,@RequestParam("password") String password) {
        return Result.of(() -> memberService.login(username,password))
                .fetch(MemberCache::setCurrentUser).logFail();
    }
}
